import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { Counter, Trend } from 'k6/metrics';

const MAX_VUS = 80;
const BASE_URL='http://localhost:8080/api';
const ATTEMPTS_PER_ITER = 1; 
const ADMIN_USER = { username: 'k6_admin@admin.com', password: 'k6_admin' };
const THEATER_ROWS = 20;          
const THEATER_COLS = 20;
const ReservationSuccess = new Counter('reservation_success');
const ReservationFail = new Counter('reservation_fail');
const Attempt = new Counter('reservation_attempt');
const TOKEN_COUNT = MAX_VUS;

export const options = {
noConnectionReuse: false, 
  noVUConnectionReuse: false,
  discardResponseBodies: true,
  scenarios: {
    stress_test: {
        executor: 'constant-arrival-rate',
        rate: 200,              
        timeUnit: '1s',
        duration: '1m',
        preAllocatedVUs: MAX_VUS / 2,   
        maxVUs: MAX_VUS,            
      },  
  },
  thresholds: {
    http_req_failed: ['rate>0.05'], 
  },
};

// 유저 생성기
function generateRandomUser() {
  return {
    username: `user_${randomString(10)}@test.com`,
    password: `password_${randomString(10)}`,
  };
}

// 테스트 데이터 (setup에서 실제 DB ID를 가져오거나 고정값 사용)
export function setup() {
  const jsonHeaders = { 'Content-Type': 'application/json' };

  const loginRes = http.post(`${BASE_URL}/login`, JSON.stringify(ADMIN_USER), { headers: jsonHeaders, responseType: 'text' });
  check(loginRes, { 'admin login': (r) => r.status === 200 });

  const adminToken = loginRes.json('data.accessToken');
  const authHeaders = { 'Content-Type': 'application/json', Authorization: `Bearer ${adminToken}` };

  const movieRes = http.post(
    `${BASE_URL}/movies`,
    JSON.stringify({ title: `k6_Test_Movie_${randomString(6)}`, duration: 120 }),
    { headers: authHeaders , responseType: 'text'}
  );
  const movieId = movieRes.json('data.movieId');

  const theaterRes = http.post(
    `${BASE_URL}/theaters`,
    JSON.stringify({ name: `k6_Theater_${randomString(6)}`, rowSize: THEATER_ROWS, colSize: THEATER_COLS }),
    { headers: authHeaders ,responseType: 'text' }
  );
  const theaterId = theaterRes.json('data.theaterId');

    // 현재 시간 + 1시간 뒤의 KST 시간 생성
    const now = new Date();
    const kstOffset = 9 * 60 * 60 * 1000; // 9시간 밀리초
    const kstTime = new Date(now.getTime() + kstOffset + (3600 * 1000)); 

    // ISO 형식에서 'Z'를 제거하고 필요한 부분만 추출 (YYYY-MM-DDTHH:mm:ss)
    const startTime = kstTime.toISOString().replace('Z', '');  const screeningRes = http.post(
    `${BASE_URL}/screenings`,
    JSON.stringify({ movieId, theaterId, startTime }),
    { headers: authHeaders , responseType: 'text'}
  );
  const screeningId = screeningRes.json('data.screeningId');
  const screeningGetRes = http.get(
    `${BASE_URL}/screenings/${screeningId}`,
    { headers: authHeaders , responseType: 'text'}
  );
  const seats = screeningGetRes.json('data.seats') || [];
  const seatPool = seats.map((s) => (typeof s === 'object' ? s.seatId : s)).filter(Boolean);

  // tokens
  const tokens = new Array(TOKEN_COUNT);
  for (let i = 0; i < TOKEN_COUNT; i++) {
    const user = generateRandomUser();
    const signupRes = http.post(`${BASE_URL}/signup`, JSON.stringify(user), { headers: jsonHeaders, responseType: 'text' });
    check(signupRes, { 'signup ok': (r) => r.status === 201 || r.status === 200 });
    tokens[i] = signupRes.json('data.accessToken');
  }

  console.log('====================================================');
  console.log(`[k6 Setup] 생성된 Screening ID: ${screeningId}`);
  console.log(`[k6 Setup] 총 좌석 수: ${seatPool.length}개`);
  console.log(`[k6 Setup] 생성된 유저 토큰 수: ${tokens.length}개`);
  console.log('====================================================');

  return { screeningId, seatPool, tokens };
}

export default function (data) {
  const token = data.tokens[(__VU - 1) % data.tokens.length];
  const headers = { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` };

  for (let i = 0; i < ATTEMPTS_PER_ITER; i++) {
    Attempt.add(1);

    const countToPick = randomIntBetween(2,5); 
    let shuffled = [...data.seatPool].sort(() => 0.5 - Math.random());
    const selectedSeats = shuffled.slice(0, countToPick);

    const payload = JSON.stringify({ 
      screeningId: data.screeningId, 
      seats: selectedSeats // 이제 여러 개의 좌석 아이디가 들어감
    });

    const res = http.post(`${BASE_URL}/reservations`, payload, { headers });

    // ... 후속 로직 (성공/실패 체크 등)
    if (res.status === 201) ReservationSuccess.add(1);
    else ReservationFail.add(1);

    check(res, { 'status is 201 or 400': (r) => r.status === 201 || r.status === 400 });    sleep(0.1);
  }
}
