import React, { useState } from 'react';
import { Container, Tabs, Tab } from 'react-bootstrap';
import YouTubeGrid from '../components/social/YouTubeGrid';
import InstagramGrid from '../components/social/InstagramGrid';
import { useSearchParams } from 'react-router-dom';

const SocialEmbed = () => {
    const [searchParams] = useSearchParams();
    const type = searchParams.get('type') || 'youtube'; // 'youtube' or 'instagram'

    return (
        <div className="bg-white min-vh-100 p-3">
            {/* No Header/Footer here suitable for iframe */}
            <h4 className="fw-bold mb-3 text-center">
                {type === 'youtube' ? 'YouTube' : 'Instagram'}
            </h4>

            {type === 'youtube' && <YouTubeGrid />}
            {type === 'instagram' && <InstagramGrid />}
        </div>
    );
};

export default SocialEmbed;
