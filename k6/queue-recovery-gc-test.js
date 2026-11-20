import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://springboot:8080';

export const options = {
    scenarios: {
        queue_recovery: {
            executor: 'shared-iterations',
            vus: 1,
            iterations: 1,
            exec: 'recover',
            startTime: '0s',
        },
        user_traffic: {
            executor: 'constant-vus',
            vus: Number(__ENV.TRAFFIC_VUS || 20),
            duration: __ENV.TRAFFIC_DURATION || '60s',
            exec: 'traffic',
            startTime: '1s',
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.05'],
        http_req_duration: ['p(95)<120000'],
    },
};

export function recover() {
    const res = http.post(`${BASE_URL}/test/queue/recover`);

    check(res, {
        'queue recovery triggered': (response) => response.status === 204,
    });
}

export function traffic() {
    const res = http.get(`${BASE_URL}/event`);

    check(res, {
        'event list is available': (response) => response.status === 200,
    });

    sleep(1);
}
