import React, { useState } from 'react';
import api from '../../services/api';

const RatingStars = ({ contentId, initialRating, initialAverageRating, initialRatingCount }) => {
    const [userRating, setUserRating] = useState(initialRating || 0);
    const [averageRating, setAverageRating] = useState(initialAverageRating || 0);
    const [ratingCount, setRatingCount] = useState(initialRatingCount || 0);
    const [hoverRating, setHoverRating] = useState(0);
    const [loading, setLoading] = useState(false);

    const handleRating = async (rating) => {
        if (loading) return;
        setLoading(true);
        try {
            const response = await api.post(`/contents/${contentId}/rating`, { rating });
            const { averageRating: newAvg, message } = response.data;
            setUserRating(rating);
            setAverageRating(newAvg);
            // If user hadn't rated before, increment count logic could be complex without backend count return
            // For now, we assume backend might return updated count or we just trust avg
            // But TechSpec says backend returns { averageRating, message } only for now.
            // Let's verify backend response structure from TechSpec.
            // TechSpec says: { "averageRating": 4.6, "message": "..." }
            // It doesn't return count. We might need to assume count increases if userRating was 0.
            if (userRating === 0) {
                setRatingCount(prev => prev + 1);
            }
        } catch (error) {
            console.error('Failed to submit rating', error);
            alert('별점을 저장하지 못했습니다.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="text-center">
            <div className="mb-2">
                <span className="display-4 fw-bold">{averageRating.toFixed(1)}</span>
                <span className="text-muted ms-2">/ 5.0 ({ratingCount}명 참여)</span>
            </div>
            <div className="d-flex justify-content-center gap-1">
                {[1, 2, 3, 4, 5].map((star) => (
                    <span
                        key={star}
                        style={{
                            cursor: 'pointer',
                            fontSize: '2rem',
                            color: (hoverRating || userRating) >= star ? '#ffc107' : '#e4e5e9',
                            transition: 'color 0.2s'
                        }}
                        onMouseEnter={() => setHoverRating(star)}
                        onMouseLeave={() => setHoverRating(0)}
                        onClick={() => handleRating(star)}
                    >
                        ★
                    </span>
                ))}
            </div>
            <div className="text-muted small mt-2">
                {userRating > 0 ? `나의 별점: ${userRating}점` : '별점을 눌러 평가해주세요'}
            </div>
        </div>
    );
};

export default RatingStars;
