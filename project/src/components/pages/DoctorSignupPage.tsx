import React, { useState } from 'react';
import { Stethoscope } from 'lucide-react';
import { Button } from '../common/Button';
import { Input } from '../common/Input';
import { Card } from '../common/Card';

interface DoctorSignupPageProps {
  onNavigate: (page: string) => void;
  onRegister: (userData: any) => Promise<boolean>;
}

export const DoctorSignupPage: React.FC<DoctorSignupPageProps> = ({ onNavigate, onRegister }) => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    specialization: '',
    experience: '',
    mobile: '',
    qualifications: '',
    hospital: '',
    consultationFee: '500'
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!formData.name || !formData.email || !formData.password || !formData.specialization || 
        !formData.experience || !formData.mobile || !formData.qualifications || !formData.hospital) {
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
        role: 'doctor',
        experience: formData.experience,
        consultationFee: parseFloat(formData.consultationFee) || 500
      });

      if (success) {
        onNavigate('doctor-dashboard');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-teal-50 flex items-center justify-center p-4 py-12">
      <div className="w-full max-w-xl">
        <Card className="p-8 shadow-2xl">
          <div className="text-center mb-6">
            <Stethoscope className="h-12 w-12 text-teal-600 mx-auto mb-3 animate-pulse" />
            <h2 className="text-3xl font-bold text-gray-900">Doctor Registration</h2>
            <p className="text-gray-600 mt-1 text-sm">Join the MediCare Plus specialist directory</p>
          </div>

          <form onSubmit={handleSubmit}>
            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4 text-sm font-medium">
                {error}
              </div>
            )}

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Full Name (Dr.)"
                value={formData.name}
                onChange={(value) => handleInputChange('name', value)}
                placeholder="Dr. Om Jain"
                required
              />

              <Input
                label="Email Address"
                type="email"
                value={formData.email}
                onChange={(value) => handleInputChange('email', value)}
                placeholder="doctor@clinic.com"
                required
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Password"
                type="password"
                value={formData.password}
                onChange={(value) => handleInputChange('password', value)}
                placeholder="Create password"
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
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Medical Specialization"
                value={formData.specialization}
                onChange={(value) => handleInputChange('specialization', value)}
                placeholder="Cardiologist, Neurologist..."
                required
              />
              
              <Input
                label="Years of Experience"
                value={formData.experience}
                onChange={(value) => handleInputChange('experience', value)}
                placeholder="e.g. 10 years"
                required
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Mobile Number"
                type="tel"
                value={formData.mobile}
                onChange={(value) => handleInputChange('mobile', value)}
                placeholder="+91 9876543210"
                required
              />
              
              <Input
                label="Consultation Fee (₹)"
                type="number"
                value={formData.consultationFee}
                onChange={(value) => handleInputChange('consultationFee', value)}
                placeholder="500"
                required
              />
            </div>

            <Input
              label="Hospital / Clinic Affiliation"
              value={formData.hospital}
              onChange={(value) => handleInputChange('hospital', value)}
              placeholder="MediCare Medical Center"
              required
            />

            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-2">Qualifications & Degrees</label>
              <textarea
                value={formData.qualifications}
                onChange={(e) => handleInputChange('qualifications', e.target.value)}
                placeholder="e.g. MBBS, MD (Cardiology)"
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent h-20 resize-none bg-white"
                required
              />
            </div>

            <Button type="submit" disabled={loading} variant="success" className="w-full mb-4 flex items-center justify-center">
              <Stethoscope className="h-4 w-4 mr-2" />
              {loading ? 'Submitting Registration...' : 'Register as Specialist Doctor'}
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
