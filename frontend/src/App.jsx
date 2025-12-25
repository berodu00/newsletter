import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/common/Layout';
import Home from './pages/Home';
import Login from './pages/Login';
import 'bootstrap/dist/css/bootstrap.min.css';

import { AuthProvider } from './contexts/AuthContext';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/" element={<Layout />}>
            <Route index element={<Home />} />
            <Route path="magazine" element={<div>Magazine List</div>} />
            <Route path="social" element={<div>Social Content</div>} />
            <Route path="events" element={<div>Events</div>} />
          </Route>
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
