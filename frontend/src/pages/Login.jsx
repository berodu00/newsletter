import React, { useState } from 'react';
import { Form, Button, Card, Container, Row, Col } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

import { useAuth } from '../contexts/AuthContext';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await login(username, password);
            navigate('/');
        } catch (error) {
            console.error('Login failed', error);
            alert('로그인 실패');
        }
    };

    return (
        <Container className="d-flex align-items-center justify-content-center" style={{ minHeight: '100vh' }}>
            <Card style={{ width: '400px' }}>
                <Card.Body>
                    <h3 className="text-center mb-4">로그인</h3>
                    <Form onSubmit={handleSubmit}>
                        <Form.Group className="mb-3">
                            <Form.Label>아이디</Form.Label>
                            <Form.Control
                                type="text"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>비밀번호</Form.Label>
                            <Form.Control
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                            />
                        </Form.Group>
                        <Button variant="primary" type="submit" className="w-100">
                            로그인
                        </Button>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default Login;
