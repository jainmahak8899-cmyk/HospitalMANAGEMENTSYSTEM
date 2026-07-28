import React, { useState } from 'react';
import { UserPlus } from 'lucide-react';
import { Button } from '../common/Button';
import { Input } from '../common/Input';
import { Card } from '../common/Card';

interface RegistrationPageProps {
  onNavigate: (page: string) => void;
  onRegister: (userData: any) => Promise<boolean>;
}

export const RegistrationPage: React.FC<RegistrationPageProps> = ({ onNavigate, onRegister }) => {
  const [formData, setFormData] = useState({
    name: '',
    age: '',
    gender: 'male',
    email: '',
    mobile: '',
    password: '',
    confirmPassword: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!formData.name || !formData.email || !formData.password || !formData.age || !formData.mobile) {
      setError('Please fill in all required fields');
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters long');
      return;
    }

    setLoading(true);
    try {
      const success = await onRegister({
        ...formData,
        role: 'patient',
        age: parseInt(formData.age, 10) || 25
      });

      if (success) {
        onNavigate('patient-dashboard');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-teal-50 flex items-center justify-center p-4 py-12">
      <div className="w-full max-w-md">
        <Card className="p-8 shadow-2xl">
          <div className="text-center mb-6">
            <UserPlus className="h-12 w-12 text-blue-600 mx-auto mb-4" />
            <h2 className="text-3xl font-bold text-gray-900">Create Patient Account</h2>
            <p className="text-gray-600 mt-1 text-sm">Get instant access to top hospital doctors</p>
          </div>

          <form onSubmit={handleSubmit}>
            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4 text-sm font-medium">
                {error}
              </div>
            )}

            <Input
              label="Full Name"
              value={formData.name}
              onChange={(value) => handleInputChange('name', value)}
              placeholder="e.g. John Doe"
              required
            />

            <div className="grid grid-cols-2 gap-4">
              <Input
                label="Age"
                type="number"
                value={formData.age}
                onChange={(value) => handleInputChange('age', value)}
                placeholder="Age"
                required
              />
              
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">Gender</label>
                <select
                  value={formData.gender}
                  onChange={(e) => handleInputChange('gender', e.target.value)}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white"
                  required
                >
                  <option value="male">Male</option>
                  <option value="female">Female</option>
                  <option value="other">Other</option>
                </select>
              </div>
            </div>

            <Input
              label="Email Address"
              type="email"
              value={formData.email}
              onChange={(value) => handleInputChange('email', value)}
              placeholder="e.g. john@example.com"
              required
            />

            <Input
              label="Mobile Number"
              type="tel"
              value={formData.mobile}
              onChange={(value) => handleInputChange('mobile', value)}
              placeholder="+91 9876543210"
              required
            />

            <Input
              label="Password"
              type="password"
              value={formData.password}
              onChange={(value) => handleInputChange('password', value)}
              placeholder="Create password (min 6 characters)"
              required
            />

            <Input
              label="Confirm Password"
              type="password"
              value={formData.confirmPassword}
              onChange={(value) => handleInputChange('confirmPassword', value)}
              placeholder="Confirm password"
              required
            />

            <Button type="submit" disabled={loading} className="w-full mb-4 flex items-center justify-center">
              <UserPlus className="h-4 w-4 mr-2" />
              {loading ? 'Creating Account...' : 'Create Patient Account'}
            </Button>
          </form>

          <div className="text-center pt-2">
            <p className="text-gray-600 text-sm">
              Already registered?{' '}
              <button
                onClick={() => onNavigate('login')}
                className="text-blue-600 hover:text-blue-700 font-semibold underline"
              >
                Sign In
              </button>
            </p>
            <button
              onClick={() => onNavigate('home')}
              className="text-gray-500 hover:text-gray-700 text-sm block mx-auto mt-3"
            >
              &larr; Back to Home Page
            </button>
          </div>
        </Card>
      </div>
    </div>
  );
};
