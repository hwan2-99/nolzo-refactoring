import http from 'k6/http';
import { check } from 'k6';
import exec from 'k6/execution';

export const options = {
    vus: 1000,
    iterations: 1000,
    thresholds: {
        http_req_duration: ['p(95)<500'],
    },
};

function login(userId) {
    const email = `${userId}@example.com`;

    const res = http.post(
        'http://springboot:8080/auth/login',
        JSON.stringify({
            email: email,
            password: '1',
        }),
        {
            headers: { 'Content-Type': 'application/json' },
        }
    );

    check(res, {
        'login successful': (r) => r.status === 200,
    });

    if (res.status !== 200) {
        console.log(`❗ Login failed for: ${email}`);
        return null;
    }

    return res.json('accessToken');
}

function reserve(token, userId) {
    if (!token) return;

    const idemKey = `USER-${userId}`;

    const res = http.post(
        'http://springboot:8080/reservations',
        JSON.stringify({
            eventId: 1,
            seats: [
                {
                    id: 1,
                    rowName: 'A',
                    seatNumber: 1,
                    seatSection: '1구역',
                    price: 150000,
                    status: 'AVAILABLE',
                },
            ],
        }),
        {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
                'Idempotency-Key': idemKey,
            },
        }
    );

    check(res, {
        'reservation successful': (r) => r.status === 200,
    });
}

export default function () {
    const userId = exec.vu.idInTest; // 1, 2, 3 ... 안전하게 사용
    const token = login(userId);

    if (token) {
        reserve(token, userId);
    }
}
