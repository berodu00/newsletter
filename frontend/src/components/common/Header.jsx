import React from 'react';
import { Navbar, Container, Nav, Button } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const Header = () => {
    const navigate = useNavigate();
    const { user, logout } = useAuth();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <Navbar bg="light" expand="lg">
            <Container>
                <Navbar.Brand as={Link} to="/">KZ Magazine</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        <Nav.Link as={Link} to="/magazine">사보</Nav.Link>
                        <Nav.Link as={Link} to="/social">소셜</Nav.Link>
                        <Nav.Link as={Link} to="/events">이벤트</Nav.Link>
                        {user && user.role === 'ADMIN' && (
                            <Nav.Link as={Link} to="/admin">관리자</Nav.Link>
                        )}
                    </Nav>
                    <Nav>
                        {user ? (
                            <div className="d-flex align-items-center gap-2">
                                <span className="text-muted">{user.name}님</span>
                                <Button variant="outline-secondary" onClick={handleLogout}>로그아웃</Button>
                            </div>
                        ) : (
                            <Button variant="outline-primary" onClick={() => navigate('/login')}>로그인</Button>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Header;
