import React from 'react';
import { Navbar, Container, Nav, Button } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';

const Header = () => {
    const navigate = useNavigate();
    // TODO: Connect with AuthContext later

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
                    </Nav>
                    <Nav>
                        <Button variant="outline-primary" onClick={() => navigate('/login')}>로그인</Button>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Header;
