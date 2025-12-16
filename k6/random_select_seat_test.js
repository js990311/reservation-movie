import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/latest/dist/bundle.js'

const BASE_URL = 'http://localhost:8080/api';
const ADMIN_USER = {
    username: 'k6_admin@admin.com', 
    password: 'k6_admin'     
};

const SuccessCount = new Counter('reservation_success');
const FailCount = new Counter('reservation_fail');

const VU_COUNT = 200; 

export const options = {
    vus: VU_COUNT,  
    iterations: VU_COUNT * 3,
    tags: {
        test_type: 'random_select_seat'
    }
};

function generateRandomUser(){
    const id = Math.floor(Math.random() * 1000000);
    const username = `user_${randomString(8)}@test.com`;
    const password = `password_${randomString(8)}`;
    return {
        username,
        password
    };
}

export function setup() {
    console.log('[Setup] Starting data generation...');
    
    const headers = { 'Content-Type': 'application/json' };

    // 사전 준비된 관리자 계정으로 접근 
    const loginRes = http.post(`${BASE_URL}/login`, JSON.stringify(ADMIN_USER), { headers });
    check(loginRes, { 'Admin Login Successful': (r) => r.status === 200 });
    
    const accessToken = loginRes.json('data.accessToken');
    const authHeaders = { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${accessToken}`
    };

    // 영화 생성 
    const moviePayload = JSON.stringify({
        title: `k6_Test_Movie_${randomString(5)}`,
        duration: 120
    });
    const movieRes = http.post(`${BASE_URL}/movies`, moviePayload, { headers: authHeaders });
    const movieId = movieRes.json('data.movieId');
    console.log(`[Setup] Movie Created: ID ${movieId}`);

    // 극장 생성 (5x5 = 25)
    const theaterPayload = JSON.stringify({
        name: `k6_Theater_${randomString(5)}`,
        rowSize: 5,
        colSize: 5
    });
    const theaterRes = http.post(`${BASE_URL}/theaters`, theaterPayload, { headers: authHeaders });
    const theaterId = theaterRes.json('data.theaterId');

    // 생성된 극장의 좌석 ID 확보 
    const targetSeatIds = theaterRes.json('data.seats'); 

    // 상영표 생성
    const startTime = new Date(Date.now() + 3600 * 1000).toISOString(); 
    const screeningPayload = JSON.stringify({
        movieId: movieId,
        theaterId: theaterId,
        startTime: startTime
    });
    const screeningRes = http.post(`${BASE_URL}/screenings`, screeningPayload, { headers: authHeaders });
    const screeningId = screeningRes.json('data.screeningId');
    console.log(`[Setup] Screening Created: ID ${screeningId}`);

    const tokens = [];

    for(let i=1;i<=VU_COUNT;i++){
        const user = generateRandomUser();
        const payload = JSON.stringify(user);
        const params = {
            headers: { 'Content-Type': 'application/json' },
        };
        const response = http.post(`${BASE_URL}/signup`, payload, params);
        tokens.push(response.json('data.accessToken')) ;
    }

    return {
        screeningId: screeningId,
        tokens: tokens
    };
}


export default function (data) {
    // 사전에 제작된 accessToken으로 접근
    const accessToken = data.tokens[__VU - 1]; 
    const screeningId = data.screeningId;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${accessToken}`,
    };

    // screenings를 받아와서 예매 가능한 좌석 찾기 
    const screeningResponse = http.get(`${BASE_URL}/screenings/${screeningId}`,  {headers});
    check(screeningResponse,{
        "get screenings success": (r) => r.status === 200,
    })
    const seats = screeningResponse.json('data.seats');
    const availableSeats = seats.filter(seat=>!seat.reserved);
    availableSeats.sort(()=>0.5-Math.random());

    if(availableSeats.length === 0){
        return;
    }
    
    const seatsToReserveCount = Math.floor(Math.random() * Math.min(availableSeats.length, 5)) + 1; // 1, 2, 3, 4, 5 중 하나
    
    const targetSeatIds = availableSeats.slice(0, seatsToReserveCount).map(seat=>seat.seatId);

    const payload = JSON.stringify({
        screeningId : screeningId,
        seats: targetSeatIds
    });

    const response = http.post(`${BASE_URL}/reservations`, payload, {headers});
    check(response, {
        '예매 성공' : (r) => r.status === 201, 
        "예매 실패" : (r) => {
            if(r.status === 400){
                const errorType = r.json('error.type');
                return errorType === 'INVALID_OR_UNAVAILABLE_SEATS';
            }
            return false;
        },
        "시나리오에서 상정하지 않는 케이스가 존재하는 지 확인" : (r) => {
            if(r.status === 400){
                const errorType = r.json('error.type');
                return errorType === 'INVALID_OR_UNAVAILABLE_SEATS';
            }
            return r.status === 201;
        }
    });


    if(response.status === 201){
        SuccessCount.add(1);
    }else if(response.status === 400){
        FailCount.add(1);
    }
}

export function handleSummary(data) {
  return {
    './reports/random_select_seats_test.html': htmlReport(data),
  }
}
