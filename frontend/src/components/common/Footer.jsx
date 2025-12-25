import React from 'react';
import { Container, Row, Col } from 'react-bootstrap';

const Footer = () => {
    return (
        <footer className="bg-light py-3 mt-auto">
            <Container>
                <Row>
                    <Col className="text-center">
                        <small className="text-muted">© 2025 Korea Zinc. All rights reserved.</small>
                    </Col>
                </Row>
            </Container>
        </footer>
    );
};

export default Footer;
