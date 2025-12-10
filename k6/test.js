import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 100, // 20명의 가상 사용자
    iterations: 100, // 총 20번의 반복
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95%의 요청이 500ms 이하로 완료되도록 설정
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
        seatIds: [1], // 좌석 ID 리스트 (여기서는 단일 좌석만)
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
    const userId = __VU;
    const token = login(userId);

    if (token) {
        reserve(token);
    }
}
