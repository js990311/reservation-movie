import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

const BASE_URL = 'http://localhost:8080/api';
const ADMIN_USER = {
    username: 'k6_admin@admin.com', 
    password: 'k6_admin'     
};

const SuccessCount = new Counter('reservation_success');
const FailCount = new Counter('reservation_fail');

const VU_COUNT = 50; 

export const options = {
    vus: VU_COUNT,             
    iterations: 50,      
    tags: {
        test_type: 'three_seat'
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

    // 영화관 생성 
    const moviePayload = JSON.stringify({
        title: `k6_Test_Movie_${randomString(5)}`,
        duration: 120
    });
    const movieRes = http.post(`${BASE_URL}/movies`, moviePayload, { headers: authHeaders });
    const movieId = movieRes.json('data.movieId');
    console.log(`[Setup] Movie Created: ID ${movieId}`);

    // 극장 생성 (10x10)
    const theaterPayload = JSON.stringify({
        name: `k6_Theater_${randomString(5)}`,
        rowSize: 10,
        colSize: 10
    });
    const theaterRes = http.post(`${BASE_URL}/theaters`, theaterPayload, { headers: authHeaders });
    const theaterId = theaterRes.json('data.theaterId');

    // 생성된 극장의 좌석 ID 확보 
    const targetSeats = theaterRes.json('data.seats'); 

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
        targetSeats: targetSeats,
        tokens: tokens
    };
}


export default function (data) {
    // 사전에 제작된 accessToken으로 접근
    const accessToken = data.tokens[__VU - 1]; 
    const targetSeats = data.targetSeats;
    const screeningId = data.screeningId;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${accessToken}`,
    };

    const payload = JSON.stringify({
        screeningId : screeningId,
        seats: [
            targetSeats[0].seatId, 
            targetSeats[1].seatId,
            targetSeats[2].seatId
        ]
    });

    const response = http.post(`${BASE_URL}/reservations`, payload, {headers});

    if(response.status === 201){
        SuccessCount.add(1);
    }else if(response.status === 400){
        FailCount.add(1);
    }

    sleep(1);
}