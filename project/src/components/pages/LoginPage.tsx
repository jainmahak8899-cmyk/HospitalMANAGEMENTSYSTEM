import React, { useState } from 'react';
import { Heart, LogIn } from 'lucide-react';
import { Button } from '../common/Button';
import { Input } from '../common/Input';
import { Card } from '../common/Card';

interface LoginPageProps {
  onNavigate: (page: string) => void;
  onLogin: (email: string, password: string, role: string) => Promise<boolean>;
}

export const LoginPage: React.FC<LoginPageProps> = ({ onNavigate, onLogin }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('patient');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    
    if (!email || !password) {
      setError('Please fill in all required fields');
      return;
    }

    setLoading(true);
    try {
      const success = await onLogin(email, password, role);
      if (!success) {
        setError('Invalid credentials or network issue. Please check email & password.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-teal-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <Card className="p-8 shadow-2xl">
          <div className="text-center mb-6">
            <Heart className="h-12 w-12 text-blue-600 mx-auto mb-4 animate-bounce" />
            <h2 className="text-3xl font-bold text-gray-900">Welcome Back</h2>
            <p className="text-gray-600 mt-2 text-sm">Sign in to your MediCare Plus account</p>
          </div>

          <form onSubmit={handleSubmit}>
            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4 text-sm font-medium">
                {error}
              </div>
            )}

            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">Login Role</label>
              <select
                value={role}
                onChange={(e) => setRole(e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white font-medium"
              >
                <option value="patient">Patient Portal</option>
                <option value="doctor">Doctor Portal</option>
                <option value="admin">Administrator Portal</option>
              </select>
            </div>

            <Input
              label="Email Address"
              type="email"
              value={email}
              onChange={setEmail}
              placeholder="e.g. john@example.com"
              required
            />

            <Input
              label="Password"
              type="password"
              value={password}
              onChange={setPassword}
              placeholder="Enter your password"
              required
            />

            <Button type="submit" disabled={loading} className="w-full mb-4 flex items-center justify-center">
              <LogIn className="h-4 w-4 mr-2" />
              {loading ? 'Authenticating...' : 'Sign In'}
            </Button>
          </form>

          <div className="text-center space-y-3 pt-2">
            <p className="text-gray-600 text-sm">
              Don't have a patient account?{' '}
              <button
                onClick={() => onNavigate('register')}
                className="text-blue-600 hover:text-blue-700 font-semibold underline"
              >
                Sign up as Patient
              </button>
            </p>
            <p className="text-gray-600 text-sm">
              Are you a healthcare specialist?{' '}
              <button
                onClick={() => onNavigate('doctor-signup')}
                className="text-teal-600 hover:text-teal-700 font-semibold underline"
              >
                Register as Doctor
              </button>
            </p>
            <button
              onClick={() => onNavigate('home')}
              className="text-gray-500 hover:text-gray-700 text-sm block mx-auto pt-2"
            >
              &larr; Back to Home Page
            </button>
          </div>
        </Card>
      </div>
    </div>
  );
};
