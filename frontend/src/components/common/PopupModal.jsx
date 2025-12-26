import React, { useState, useEffect } from 'react';
import { Modal, Button, Form, Image } from 'react-bootstrap';
import api from '../../services/api';
import { useLocation } from 'react-router-dom';

const PopupModal = () => {
    const [popups, setPopups] = useState([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [show, setShow] = useState(false);
    const [dontShowToday, setDontShowToday] = useState(false);
    const location = useLocation();

    useEffect(() => {
        // Only fetch/show popups on the Home page ('/')
        if (location.pathname === '/') {
            checkAndLoadPopups();
        } else {
            setShow(false);
        }
    }, [location.pathname]);

    const checkAndLoadPopups = async () => {
        // Check localStorage for global hidden flag or specific popup hidden flags
        const hiddenUntil = localStorage.getItem('popup_hidden_until');
        if (hiddenUntil && new Date() < new Date(hiddenUntil)) {
            return;
        }

        try {
            const response = await api.get('/popups'); // Public endpoint returns active popups
            const activePopups = response.data.filter(p => !isPopupHidden(p.popupId));

            if (activePopups.length > 0) {
                setPopups(activePopups);
                setShow(true);
            }
        } catch (error) {
            console.error('Failed to load popups:', error);
        }
    };

    const isPopupHidden = (id) => {
        // Option: Per-popup hiding?
        // Requirement says "오늘 하루 보지 않기" which usually applies to all or the current daily set.
        // Let's implement global daily hide for simplicity, or per-popup if IDs are stored.
        // Let's stick to global "Don't show ANY popups for today" if checked.
        return false;
    };

    const handleClose = () => {
        if (dontShowToday) {
            const tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            tomorrow.setHours(0, 0, 0, 0); // Reset to next midnight
            localStorage.setItem('popup_hidden_until', tomorrow.toISOString());
        }

        // If there are multiple popups, show next? 
        // Or just close information. Carousel popups are better for multiple.
        // Let's implement a simple sequence or just show the first one for now.
        // If we want to show multiple, we could cycle through them.
        // Let's just close the modal for now.
        setShow(false);
    };

    if (!show || popups.length === 0) return null;

    const currentPopup = popups[0]; // Displaying only the first one or logic to cycle

    return (
        <Modal show={show} onHide={handleClose} centered>
            <Modal.Body className="p-0 position-relative">
                <Button
                    variant="light"
                    className="position-absolute top-0 end-0 m-2 rounded-circle shadow-sm"
                    style={{ zIndex: 10, width: '30px', height: '30px', padding: 0 }}
                    onClick={handleClose}
                >
                    &times;
                </Button>
                {currentPopup.imageUrl && (
                    <Image src={currentPopup.imageUrl} fluid style={{ width: '100%' }} />
                )}
                <div className="p-3">
                    <h5>{currentPopup.title}</h5>
                </div>
            </Modal.Body>
            <Modal.Footer className="justify-content-between">
                <Form.Check
                    type="checkbox"
                    id="dont-show-today"
                    label="오늘 하루 보지 않기"
                    checked={dontShowToday}
                    onChange={(e) => setDontShowToday(e.target.checked)}
                />
                <Button variant="secondary" size="sm" onClick={handleClose}>
                    닫기
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default PopupModal;
