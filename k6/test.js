import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 500,
    iterations: 500,
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95%의 요청이 500ms 이하로 완료되어야 한다는 조건
    },
};

function login(userId) {
    const email = `${userId}@example.com`;

    const res = http.post('http://springboot:8080/auth/login', JSON.stringify({
        email: email,
        password: '1',
    }), {
        headers: { 'Content-Type': 'application/json' },
    });

    check(res, {
        'login successful': (r) => r.status === 200,
    });

    if (res.status !== 200) {
        console.log(`❗ Login failed for: ${email}`);
        return null;
    }

    return res.json('accessToken');
}

function reserve(token) {
    if (!token) return;

    const res = http.post('http://springboot:8080/reservations', JSON.stringify({
        eventId: 1,
        seats: [ {
            id: 1,
            rowName: 'A',
            seatNumber: 1,
            seatSection: '1구역',
            price: 150000,
            status: 'AVAILABLE' } ],
    }), {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
        },
    });

    check(res, {
        'reservation successful': (r) => r.status === 200,
    });
}

export default function () {
    const userId = __VU;   // <-- 여기 핵심!!!!!
    const token = login(userId);
    reserve(token);
}
