import React, { useState } from 'react';
import { Button, ButtonGroup } from 'react-bootstrap';
import api from '../../services/api';

const REACTION_TYPES = {
    LIKE: { label: '👍 좋아요', color: 'outline-primary' },
    FUNNY: { label: '😄 재밌어요', color: 'outline-warning' },
    SAD: { label: '😢 슬퍼요', color: 'outline-info' },
    ANGRY: { label: '😡 화나요', color: 'outline-danger' }
};

const ReactionButtons = ({ contentId, initialReactions, initialUserReaction }) => {
    const [reactions, setReactions] = useState(initialReactions || { LIKE: 0, FUNNY: 0, SAD: 0, ANGRY: 0 });
    const [userReaction, setUserReaction] = useState(initialUserReaction);
    const [loading, setLoading] = useState(false);

    const handleReaction = async (type) => {
        if (loading) return;
        setLoading(true);
        try {
            const response = await api.post(`/contents/${contentId}/reaction`, { reactionType: type });
            const { currentReaction, reactions: newReactions } = response.data;
            setUserReaction(currentReaction);
            setReactions(newReactions);
        } catch (error) {
            console.error('Failed to update reaction', error);
            alert('반응을 저장하지 못했습니다.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="d-flex justify-content-center gap-2 flex-wrap">
            {Object.keys(REACTION_TYPES).map(type => (
                <Button
                    key={type}
                    variant={userReaction === type ? REACTION_TYPES[type].color.replace('outline-', '') : REACTION_TYPES[type].color}
                    onClick={() => handleReaction(type)}
                    disabled={loading}
                    className="d-flex align-items-center gap-2"
                >
                    <span>{REACTION_TYPES[type].label}</span>
                    <span className="badge bg-light text-dark rounded-pill">
                        {reactions[type] || 0}
                    </span>
                </Button>
            ))}
        </div>
    );
};

export default ReactionButtons;
