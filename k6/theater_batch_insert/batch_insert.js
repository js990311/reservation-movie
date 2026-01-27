import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { Counter, Trend } from 'k6/metrics';

const BASE_URL='https://movie.rejs.link/api';
// const BASE_URL='http://localhost:8080/api';
const ADMIN_USER = { username: 'k6_admin@admin.com', password: 'k6_admin' };
const THEATER_ROWS = 20;          
const THEATER_COLS = 20;

export const options = {
noConnectionReuse: false, 
  noVUConnectionReuse: false,
  discardResponseBodies: true,
  scenarios: {
    stress_test: {
        executor: 'constant-arrival-rate',
        rate: 10,              
        timeUnit: '1s',
        duration: '30s',       
        preAllocatedVUs: 10,   
        maxVUs: 20,            
      },  
  },
  thresholds: {
    http_req_failed: ['rate<0.05'], 
    http_req_duration: ['p(99)<300'], 
  },
};

export function setup() {
  const jsonHeaders = { 'Content-Type': 'application/json' };

  const loginRes = http.post(`${BASE_URL}/login`, JSON.stringify(ADMIN_USER), { headers: jsonHeaders, responseType: 'text' });
  check(loginRes, { 'admin login': (r) => r.status === 200 });

  const adminToken = loginRes.json('data.tokens.accessToken.token');
  return {adminToken}
}

export default function (data) {
const adminToken = data.adminToken;
const authHeaders = { 'Content-Type': 'application/json', Authorization: `Bearer ${adminToken}` };
  const theaterRes = http.post(
    `${BASE_URL}/theaters/test`,
    JSON.stringify({ name: `k6_Theater_${randomString(6)}`, rowSize: THEATER_ROWS, colSize: THEATER_COLS }),
    { headers: authHeaders ,responseType: 'text' }
  );

  check(theaterRes, {
    'theater created (201)': (r) => r.status === 201,
  });
  sleep(0.1);
}
