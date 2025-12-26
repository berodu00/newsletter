import React, { useState } from 'react';
import { Container, Tabs, Tab } from 'react-bootstrap';
import YouTubeGrid from '../components/social/YouTubeGrid';
import InstagramGrid from '../components/social/InstagramGrid';

const Social = () => {
    const [key, setKey] = useState('youtube');

    return (
        <Container className="py-5">
            <div className="mb-5 text-center">
                <h2 className="fw-bold mb-3">Social Lounge</h2>
                <p className="text-muted">고려아연의 생생한 소식을 영상과 사진으로 만나보세요.</p>
            </div>

            <Tabs
                id="social-tabs"
                activeKey={key}
                onSelect={(k) => setKey(k)}
                className="mb-4 justify-content-center border-bottom-0"
                variant="pills"
            >
                <Tab eventKey="youtube" title="YouTube" className="px-2">
                    <div className="pt-3">
                        <YouTubeGrid />
                    </div>
                </Tab>
                <Tab eventKey="instagram" title="Instagram" className="px-2">
                    <div className="pt-3">
                        <InstagramGrid />
                    </div>
                </Tab>
            </Tabs>
        </Container>
    );
};

export default Social;
