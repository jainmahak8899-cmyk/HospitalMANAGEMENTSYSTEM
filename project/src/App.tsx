import { useState, useEffect } from 'react';
import { User, Doctor, ToastMessage } from './types';
import { ToastContainer } from './components/common/Toast';
import { LandingPage } from './components/pages/LandingPage';
import { LoginPage } from './components/pages/LoginPage';
import { RegistrationPage } from './components/pages/RegistrationPage';
import { DoctorSignupPage } from './components/pages/DoctorSignupPage';
import { PatientDashboard } from './components/dashboards/PatientDashboard';
import { DoctorDashboard } from './components/dashboards/DoctorDashboard';
import { AdminDashboard } from './components/dashboards/AdminDashboard';

function App() {
  const [currentPage, setCurrentPage] = useState('home');
  const [user, setUser] = useState<User | null>(null);
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  const addToast = (type: 'success' | 'error' | 'info', message: string) => {
    const id = Date.now().toString() + Math.random().toString().substring(2, 5);
    const newToast: ToastMessage = { id, type, message };
    setToasts(prev => [...prev, newToast]);

    setTimeout(() => {
      setToasts(prev => prev.filter(t => t.id !== id));
    }, 4000);
  };

  const dismissToast = (id: string) => {
    setToasts(prev => prev.filter(t => t.id !== id));
  };

  useEffect(() => {
    try {
      const savedUser = localStorage.getItem('currentUser');
      if (savedUser) {
        const userData: User = JSON.parse(savedUser);
        if (userData && userData.role) {
          setUser(userData);
          setCurrentPage(`${userData.role}-dashboard`);
        }
      }
    } catch (e) {
      console.error('Failed to parse saved user:', e);
      localStorage.removeItem('currentUser');
    }
  }, []);

  const login = async (email: string, password: string, role: string): Promise<boolean> => {
    try {
      const response = await fetch('http://localhost:8080/medicare-api/api/users/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password, role })
      });

      if (!response.ok) {
        return loginLocal(email, password, role);
      }

      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        const data = await response.json();
        if (data.success && data.user) {
          setUser(data.user);
          localStorage.setItem('currentUser', JSON.stringify(data.user));
          setCurrentPage(`${data.user.role}-dashboard`);
          addToast('success', `Welcome back, ${data.user.name}!`);
          return true;
        }
      }
      return loginLocal(email, password, role);
    } catch (error) {
      console.error('API login failed, attempting local authentication:', error);
      return loginLocal(email, password, role);
    }
  };

  const loginLocal = (email: string, password: string, role: string): boolean => {
    // Default admin credentials
    if (role === 'admin' && email === 'admin@medicare.com' && password === 'admin123') {
      const adminUser: User = {
        id: 'admin',
        name: 'Administrator',
        email: 'admin@medicare.com',
        role: 'admin'
      };
      setUser(adminUser);
      localStorage.setItem('currentUser', JSON.stringify(adminUser));
      setCurrentPage('admin-dashboard');
      addToast('success', 'Logged in as Administrator');
      return true;
    }

    // Default doctor credentials
    if (role === 'doctor') {
      const doctors = [
        { id: '1', email: 'om@medicare.com', password: 'doctor123', name: 'Dr. Om Jain' },
        { id: '2', email: 'michael@medicare.com', password: 'doctor123', name: 'Dr. Michael Chen' },
        { id: '3', email: 'priya@medicare.com', password: 'doctor123', name: 'Dr. Priya Sharma' },
        { id: '4', email: 'james@medicare.com', password: 'doctor123', name: 'Dr. James Wilson' }
      ];

      const doctor = doctors.find(d => d.email === email && d.password === password);
      if (doctor) {
        const doctorUser: User = {
          id: doctor.id,
          name: doctor.name,
          email: doctor.email,
          role: 'doctor'
        };
        setUser(doctorUser);
        localStorage.setItem('currentUser', JSON.stringify(doctorUser));
        setCurrentPage('doctor-dashboard');
        addToast('success', `Welcome, ${doctor.name}`);
        return true;
      }
    }

    // Check registered patients and doctors in localStorage
    if (role === 'patient' || role === 'doctor') {
      const savedUsers = localStorage.getItem('users');
      if (savedUsers) {
        try {
          const users: User[] = JSON.parse(savedUsers);
          const foundUser = users.find((u: User) => u.email === email && u.role === role);
          if (foundUser) {
            setUser(foundUser);
            localStorage.setItem('currentUser', JSON.stringify(foundUser));
            setCurrentPage(`${role}-dashboard`);
            addToast('success', `Welcome back, ${foundUser.name}!`);
            return true;
          }
        } catch (e) {
          console.error('Error parsing stored users:', e);
        }
      }
    }

    return false;
  };

  const register = async (userData: any): Promise<boolean> => {
    try {
      const response = await fetch('http://localhost:8080/medicare-api/api/users/', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData)
      });

      if (!response.ok) {
        return registerLocal(userData);
      }

      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        const data = await response.json();
        if (data.success && data.user) {
          setUser(data.user);
          localStorage.setItem('currentUser', JSON.stringify(data.user));
          setCurrentPage(`${data.user.role}-dashboard`);
          addToast('success', 'Registration successful!');
          return true;
        }
      }
      return registerLocal(userData);
    } catch (error) {
      console.error('API registration failed, attempting local registration:', error);
      return registerLocal(userData);
    }
  };

  const registerLocal = (userData: any): boolean => {
    try {
      const newUser: User = {
        id: Date.now().toString(),
        ...userData
      };

      const savedUsers = localStorage.getItem('users');
      const users: User[] = savedUsers ? JSON.parse(savedUsers) : [];
      
      if (users.some((u: User) => u.email === userData.email)) {
        addToast('error', 'An account with this email already exists!');
        return false;
      }

      users.push(newUser);
      localStorage.setItem('users', JSON.stringify(users));
      
      if (userData.role === 'doctor') {
        const newDoctor: Doctor = {
          id: newUser.id,
          name: userData.name,
          specialization: userData.specialization,
          experience: userData.experience,
          email: userData.email,
          mobile: userData.mobile,
          hospital: userData.hospital,
          consultationFee: userData.consultationFee
        };

        const savedDoctors = localStorage.getItem('doctors');
        const doctors: Doctor[] = savedDoctors ? JSON.parse(savedDoctors) : [];
        doctors.push(newDoctor);
        localStorage.setItem('doctors', JSON.stringify(doctors));
      }
      
      setUser(newUser);
      localStorage.setItem('currentUser', JSON.stringify(newUser));
      setCurrentPage(`${newUser.role}-dashboard`);
      addToast('success', 'Account created successfully!');
      return true;
    } catch (error) {
      console.error('Registration error:', error);
      addToast('error', 'Registration failed. Please try again.');
      return false;
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('currentUser');
    setCurrentPage('home');
    addToast('info', 'Logged out successfully');
  };

  return (
    <div className="min-h-screen bg-gray-50 text-gray-900 font-sans antialiased">
      <ToastContainer toasts={toasts} onDismiss={dismissToast} />

      {currentPage === 'home' && <LandingPage onNavigate={setCurrentPage} />}
      {currentPage === 'login' && <LoginPage onNavigate={setCurrentPage} onLogin={login} />}
      {currentPage === 'register' && <RegistrationPage onNavigate={setCurrentPage} onRegister={register} />}
      {currentPage === 'doctor-signup' && <DoctorSignupPage onNavigate={setCurrentPage} onRegister={register} />}

      {currentPage === 'patient-dashboard' && user?.role === 'patient' && (
        <PatientDashboard user={user} onNavigate={setCurrentPage} onLogout={logout} addToast={addToast} />
      )}

      {currentPage === 'doctor-dashboard' && user?.role === 'doctor' && (
        <DoctorDashboard user={user} onLogout={logout} addToast={addToast} />
      )}

      {currentPage === 'admin-dashboard' && user?.role === 'admin' && (
        <AdminDashboard user={user} onLogout={logout} addToast={addToast} />
      )}
    </div>
  );
}

export default App;
