import React, { useState, useEffect } from 'react';
import { Calendar, Clock, Heart, Users, UserPlus, Trash2, Shield } from 'lucide-react';
import { User as UserType, Appointment, Doctor } from '../../types';
import { Button } from '../common/Button';
import { Input } from '../common/Input';
import { Card } from '../common/Card';

interface AdminDashboardProps {
  user: UserType;
  onLogout: () => void;
  addToast: (type: 'success' | 'error' | 'info', message: string) => void;
}

export const AdminDashboard: React.FC<AdminDashboardProps> = ({ onLogout, addToast }) => {
  const [activeTab, setActiveTab] = useState('manage-doctors');
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [showAddDoctor, setShowAddDoctor] = useState(false);
  
  const [doctorForm, setDoctorForm] = useState({
    name: '',
    specialization: '',
    experience: '',
    email: '',
    mobile: '',
    password: ''
  });

  useEffect(() => {
    if (!showAddDoctor) {
      setDoctorForm({
        name: '',
        specialization: '',
        experience: '',
        email: '',
        mobile: '',
        password: ''
      });
    }
  }, [showAddDoctor]);

  useEffect(() => {
    const savedDoctors = localStorage.getItem('doctors');
    if (savedDoctors) {
      setDoctors(JSON.parse(savedDoctors));
    } else {
      const defaultDoctors: Doctor[] = [
        { id: '1', name: 'Dr. Om Jain', specialization: 'Cardiologist', experience: '10 years', email: 'om@clinic.com', mobile: '+91 9876543211' },
        { id: '2', name: 'Dr. Ritesh Bindu', specialization: 'Neurologist', experience: '25 years', email: 'ritesh@clinic.com', mobile: '+91 9876543212' },
        { id: '3', name: 'Dr. Priya Sharma', specialization: 'Dermatologist', experience: '12 years', email: 'priya@clinic.com', mobile: '+91 9876543213' },
        { id: '4', name: 'Dr. Siddhnat Bora', specialization: 'Orthopedic', experience: '15 years', email: 'siddhnat@clinic.com', mobile: '+91 9876543214' }
      ];
      localStorage.setItem('doctors', JSON.stringify(defaultDoctors));
      setDoctors(defaultDoctors);
    }

    const savedAppointments = localStorage.getItem('appointments');
    if (savedAppointments) {
      setAppointments(JSON.parse(savedAppointments));
    }
  }, []);

  const handleAddDoctor = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!doctorForm.name || !doctorForm.specialization || !doctorForm.experience || !doctorForm.email || !doctorForm.mobile || !doctorForm.password) {
      addToast('error', 'Please fill in all fields');
      return;
    }

    const newDoctor: Doctor = {
      id: Date.now().toString(),
      ...doctorForm
    };

    const updatedDoctors = [...doctors, newDoctor];
    setDoctors(updatedDoctors);
    localStorage.setItem('doctors', JSON.stringify(updatedDoctors));
    
    setShowAddDoctor(false);
    addToast('success', `Added ${newDoctor.name} to doctor directory`);
  };

  const handleDeleteDoctor = (doctorId: string) => {
    if (window.confirm('Are you sure you want to remove this doctor?')) {
      const updatedDoctors = doctors.filter(d => d.id !== doctorId);
      setDoctors(updatedDoctors);
      localStorage.setItem('doctors', JSON.stringify(updatedDoctors));
      addToast('info', 'Doctor directory updated');
    }
  };

  const ManageDoctorsTab = () => (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h3 className="text-xl font-semibold text-gray-900">Manage Doctor Directory ({doctors.length})</h3>
        <Button onClick={() => setShowAddDoctor(true)} className="flex items-center">
          <UserPlus className="h-4 w-4 mr-2" />
          Add New Doctor
        </Button>
      </div>

      {showAddDoctor && (
        <Card className="p-6 mb-6">
          <h4 className="text-lg font-semibold mb-4 text-gray-900">Add New Doctor Profile</h4>
          <form onSubmit={handleAddDoctor}>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Doctor Name"
                value={doctorForm.name}
                onChange={(value) => setDoctorForm(prev => ({ ...prev, name: value }))}
                placeholder="Dr. Full Name"
                required
              />
              <Input
                label="Specialization"
                value={doctorForm.specialization}
                onChange={(value) => setDoctorForm(prev => ({ ...prev, specialization: value }))}
                placeholder="Cardiologist, General..."
                required
              />
              <Input
                label="Experience"
                value={doctorForm.experience}
                onChange={(value) => setDoctorForm(prev => ({ ...prev, experience: value }))}
                placeholder="10 years"
                required
              />
              <Input
                label="Mobile"
                type="tel"
                value={doctorForm.mobile}
                onChange={(value) => setDoctorForm(prev => ({ ...prev, mobile: value }))}
                placeholder="+91 9876543210"
                required
              />
              <Input
                label="Email"
                type="email"
                value={doctorForm.email}
                onChange={(value) => setDoctorForm(prev => ({ ...prev, email: value }))}
                placeholder="doctor@clinic.com"
                required
              />
              <Input
                label="Password"
                type="password"
                value={doctorForm.password}
                onChange={(value) => setDoctorForm(prev => ({ ...prev, password: value }))}
                placeholder="Temporary login password"
                required
              />
            </div>
            
            <div className="flex justify-end space-x-3 mt-4">
              <Button variant="secondary" onClick={() => setShowAddDoctor(false)}>
                Cancel
              </Button>
              <Button type="submit">
                Save Doctor Profile
              </Button>
            </div>
          </form>
        </Card>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {doctors.map(doc => (
          <Card key={doc.id} className="p-6">
            <div className="flex justify-between items-start">
              <div>
                <h4 className="text-lg font-semibold text-gray-900">{doc.name}</h4>
                <p className="text-blue-600 font-medium text-sm">{doc.specialization}</p>
                <div className="text-xs text-gray-500 mt-2 space-y-1">
                  <p>Experience: {doc.experience}</p>
                  <p>Email: {doc.email}</p>
                  <p>Mobile: {doc.mobile}</p>
                </div>
              </div>
              
              <button
                onClick={() => handleDeleteDoctor(doc.id)}
                className="text-rose-600 hover:bg-rose-50 p-2 rounded-lg transition-colors"
                title="Remove Doctor"
              >
                <Trash2 className="h-5 w-5" />
              </button>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );

  const ViewAllAppointmentsTab = () => (
    <div>
      <h3 className="text-xl font-semibold mb-6 text-gray-900">System Appointments Log ({appointments.length})</h3>
      
      {appointments.length === 0 ? (
        <Card className="p-8 text-center">
          <Calendar className="h-12 w-12 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-600 font-medium">No system appointments recorded yet.</p>
        </Card>
      ) : (
        <div className="space-y-4">
          {appointments.map(apt => (
            <Card key={apt.id} className="p-6">
              <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                <div>
                  <div className="flex items-center space-x-2">
                    <h4 className="font-semibold text-gray-900">Patient: {apt.patientName}</h4>
                    <span className="text-gray-400">&rarr;</span>
                    <h4 className="font-semibold text-blue-600">Doctor: {apt.doctorName}</h4>
                  </div>
                  
                  <div className="flex items-center text-gray-600 text-xs mt-2 space-x-4">
                    <span className="flex items-center"><Calendar className="h-3.5 w-3.5 mr-1" />{apt.date}</span>
                    <span className="flex items-center"><Clock className="h-3.5 w-3.5 mr-1" />{apt.time}</span>
                  </div>
                  
                  <p className="text-xs text-gray-700 mt-2"><span className="font-semibold">Reason:</span> {apt.reason}</p>
                </div>

                <span className={`px-3 py-1 rounded-full text-xs font-bold uppercase ${
                  apt.status === 'approved' ? 'bg-emerald-100 text-emerald-800' :
                  apt.status === 'pending' ? 'bg-amber-100 text-amber-800' :
                  apt.status === 'cancelled' ? 'bg-rose-100 text-rose-800' :
                  'bg-blue-100 text-blue-800'
                }`}>
                  {apt.status}
                </span>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-2">
              <Heart className="h-8 w-8 text-blue-600" />
              <span className="text-xl font-bold text-gray-900">MediCare Admin Portal</span>
            </div>
            
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2 bg-purple-50 px-3 py-1.5 rounded-lg border border-purple-100">
                <Shield className="h-5 w-5 text-purple-600" />
                <span className="text-gray-900 font-semibold text-sm">System Administrator</span>
              </div>
              <Button onClick={onLogout} variant="secondary">
                Logout
              </Button>
            </div>
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex flex-col lg:flex-row gap-8">
          <div className="lg:w-1/4">
            <Card className="p-4">
              <nav className="space-y-2">
                <button
                  onClick={() => setActiveTab('manage-doctors')}
                  className={`w-full text-left px-4 py-3 rounded-lg font-medium transition-colors flex items-center ${
                    activeTab === 'manage-doctors' ? 'bg-blue-100 text-blue-700' : 'text-gray-700 hover:bg-gray-100'
                  }`}
                >
                  <Users className="h-4 w-4 mr-3" />
                  Manage Doctors
                </button>
                <button
                  onClick={() => setActiveTab('view-appointments')}
                  className={`w-full text-left px-4 py-3 rounded-lg font-medium transition-colors flex items-center ${
                    activeTab === 'view-appointments' ? 'bg-blue-100 text-blue-700' : 'text-gray-700 hover:bg-gray-100'
                  }`}
                >
                  <Calendar className="h-4 w-4 mr-3" />
                  All Appointments Log
                </button>
              </nav>
            </Card>
          </div>

          <div className="lg:w-3/4">
            {activeTab === 'manage-doctors' && <ManageDoctorsTab />}
            {activeTab === 'view-appointments' && <ViewAllAppointmentsTab />}
          </div>
        </div>
      </div>
    </div>
  );
};
