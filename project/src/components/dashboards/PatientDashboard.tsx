import React, { useState, useEffect } from 'react';
import { Calendar, Clock, Heart, User, Search, Stethoscope, Printer, FileText, X, AlertCircle, Save } from 'lucide-react';
import { User as UserType, Appointment, Doctor } from '../../types';
import { Button } from '../common/Button';
import { Input } from '../common/Input';
import { Card } from '../common/Card';

interface PatientDashboardProps {
  user: UserType;
  onNavigate: (page: string) => void;
  onLogout: () => void;
  addToast: (type: 'success' | 'error' | 'info', message: string) => void;
}

const PREDEFINED_SLOTS = [
  '09:00 AM',
  '10:30 AM',
  '11:45 AM',
  '02:00 PM',
  '03:30 PM',
  '05:00 PM'
];

export const PatientDashboard: React.FC<PatientDashboardProps> = ({
  user,
  onLogout,
  addToast
}) => {
  const [activeTab, setActiveTab] = useState('book-appointment');
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [allSystemAppointments, setAllSystemAppointments] = useState<Appointment[]>([]);
  const [selectedPrescriptionApt, setSelectedPrescriptionApt] = useState<Appointment | null>(null);

  // Patient Profile State
  const [profileData, setProfileData] = useState({
    name: user.name || '',
    age: user.age ? user.age.toString() : '',
    gender: user.gender || 'male',
    mobile: user.mobile || '',
    bloodGroup: user.bloodGroup || 'A+',
    allergies: user.allergies || '',
    medicalHistory: user.medicalHistory || '',
    emergencyContact: user.emergencyContact || ''
  });

  useEffect(() => {
    const savedAppointments = localStorage.getItem('appointments');
    if (savedAppointments) {
      const parsed: Appointment[] = JSON.parse(savedAppointments);
      setAllSystemAppointments(parsed);
      setAppointments(parsed.filter((apt: Appointment) => apt.patientId === user.id));
    }
  }, [user.id]);

  const handleSaveProfile = (e: React.FormEvent) => {
    e.preventDefault();
    const updatedUser: UserType = {
      ...user,
      name: profileData.name,
      age: parseInt(profileData.age, 10) || user.age,
      gender: profileData.gender,
      mobile: profileData.mobile,
      bloodGroup: profileData.bloodGroup,
      allergies: profileData.allergies,
      medicalHistory: profileData.medicalHistory,
      emergencyContact: profileData.emergencyContact
    };

    localStorage.setItem('currentUser', JSON.stringify(updatedUser));
    
    // Update in users array as well
    const savedUsers = localStorage.getItem('users');
    if (savedUsers) {
      const usersList: UserType[] = JSON.parse(savedUsers);
      const updatedList = usersList.map(u => u.id === user.id ? updatedUser : u);
      localStorage.setItem('users', JSON.stringify(updatedList));
    }

    addToast('success', 'Medical profile updated successfully!');
  };

  const BookAppointmentTab = () => {
    const [formData, setFormData] = useState({
      doctorId: '',
      date: new Date().toISOString().split('T')[0],
      time: '',
      reason: ''
    });
    const [doctors, setDoctors] = useState<Doctor[]>([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [selectedSpecialization, setSelectedSpecialization] = useState('All');

    useEffect(() => {
      const savedDoctors = localStorage.getItem('doctors');
      if (savedDoctors) {
        setDoctors(JSON.parse(savedDoctors));
      } else {
        const defaultDoctors: Doctor[] = [
          { id: '1', name: 'Dr. Om Jain', specialization: 'Cardiologist', experience: '10 years', email: 'om@clinic.com', mobile: '+91 9876543211', hospital: 'City Heart Hospital', consultationFee: 800 },
          { id: '2', name: 'Dr. Ritesh Bindu', specialization: 'Neurologist', experience: '25 years', email: 'ritesh@clinic.com', mobile: '+91 9876543212', hospital: 'Brain & Spine Institute', consultationFee: 1200 },
          { id: '3', name: 'Dr. Priya Sharma', specialization: 'Dermatologist', experience: '12 years', email: 'priya@clinic.com', mobile: '+91 9876543213', hospital: 'Skin Care Care', consultationFee: 600 },
          { id: '4', name: 'Dr. Siddhnat Bora', specialization: 'Orthopedic', experience: '15 years', email: 'siddhnat@clinic.com', mobile: '+91 9876543214', hospital: 'Bone & Joint Clinic', consultationFee: 700 }
        ];
        localStorage.setItem('doctors', JSON.stringify(defaultDoctors));
        setDoctors(defaultDoctors);
      }
    }, []);

    const specializations = ['All', ...Array.from(new Set(doctors.map(d => d.specialization)))];

    const filteredDoctors = doctors.filter(doc => {
      const matchesSearch = doc.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                            doc.specialization.toLowerCase().includes(searchQuery.toLowerCase()) ||
                            (doc.hospital && doc.hospital.toLowerCase().includes(searchQuery.toLowerCase()));
      const matchesSpec = selectedSpecialization === 'All' || doc.specialization === selectedSpecialization;
      return matchesSearch && matchesSpec;
    });

    // Check which slots are already booked for selected doctor & date
    const bookedSlotsForDoctorAndDate = allSystemAppointments
      .filter(apt => apt.doctorId === formData.doctorId && apt.date === formData.date && apt.status !== 'cancelled')
      .map(apt => apt.time);

    const handleSubmit = (e: React.FormEvent) => {
      e.preventDefault();
      
      if (!formData.doctorId || !formData.date || !formData.time || !formData.reason) {
        addToast('error', 'Please select a doctor, date, time slot, and reason');
        return;
      }

      if (bookedSlotsForDoctorAndDate.includes(formData.time)) {
        addToast('error', 'This time slot is already booked. Please pick another slot.');
        return;
      }

      const selectedDoctor = doctors.find(d => d.id === formData.doctorId);
      if (!selectedDoctor) {
        addToast('error', 'Please select a valid doctor');
        return;
      }

      const newAppointment: Appointment = {
        id: Date.now().toString(),
        patientId: user.id,
        doctorId: formData.doctorId,
        patientName: profileData.name || user.name || 'Patient',
        doctorName: selectedDoctor.name,
        date: formData.date,
        time: formData.time,
        reason: formData.reason,
        status: 'pending',
        createdAt: new Date().toISOString()
      };

      const updatedAllAppointments = [...allSystemAppointments, newAppointment];
      localStorage.setItem('appointments', JSON.stringify(updatedAllAppointments));

      setAllSystemAppointments(updatedAllAppointments);
      setAppointments(prev => [...prev, newAppointment]);
      setFormData({ doctorId: '', date: new Date().toISOString().split('T')[0], time: '', reason: '' });
      addToast('success', `Appointment request sent to ${selectedDoctor.name} for ${newAppointment.time}!`);
    };

    return (
      <div className="space-y-6">
        {/* Doctor Search & Filter Bar */}
        <Card className="p-6">
          <h3 className="text-xl font-semibold mb-4 text-gray-900 flex items-center">
            <Search className="h-5 w-5 text-blue-600 mr-2" />
            Search & Filter Specialists
          </h3>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Search Doctor or Hospital</label>
              <div className="relative">
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Search by doctor name or clinic..."
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white"
                />
                <Search className="h-5 w-5 text-gray-400 absolute left-3 top-3.5" />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Specialization Filter</label>
              <select
                value={selectedSpecialization}
                onChange={(e) => setSelectedSpecialization(e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white"
              >
                {specializations.map((spec, i) => (
                  <option key={i} value={spec}>{spec}</option>
                ))}
              </select>
            </div>
          </div>
        </Card>

        {/* Appointment Form Card */}
        <Card className="p-6">
          <h3 className="text-xl font-semibold mb-6 text-gray-900 flex items-center">
            <Calendar className="h-5 w-5 text-blue-600 mr-2" />
            Book New Appointment
          </h3>
          
          <form onSubmit={handleSubmit}>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">Select Specialist Doctor</label>
              <select
                value={formData.doctorId}
                onChange={(e) => setFormData(prev => ({ ...prev, doctorId: e.target.value, time: '' }))}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white font-medium"
                required
              >
                <option value="">Choose a doctor ({filteredDoctors.length} available)</option>
                {filteredDoctors.map(doctor => (
                  <option key={doctor.id} value={doctor.id}>
                    {doctor.name} - {doctor.specialization} ({doctor.experience}) {doctor.consultationFee ? `[₹${doctor.consultationFee}]` : ''}
                  </option>
                ))}
              </select>
            </div>

            <div className="mb-4">
              <Input
                label="Appointment Date"
                type="date"
                value={formData.date}
                min={new Date().toISOString().split('T')[0]}
                onChange={(value) => setFormData(prev => ({ ...prev, date: value, time: '' }))}
                required
              />
            </div>

            {/* Interactive Time Slot Picker */}
            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-2 flex items-center justify-between">
                <span>Select Available Time Slot:</span>
                {formData.time && <span className="text-blue-600 font-semibold text-xs">Selected: {formData.time}</span>}
              </label>

              {!formData.doctorId ? (
                <p className="text-xs text-gray-500 italic bg-gray-50 p-3 rounded-lg border border-dashed">Please select a doctor first to view available time slots.</p>
              ) : (
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                  {PREDEFINED_SLOTS.map(slot => {
                    const isBooked = bookedSlotsForDoctorAndDate.includes(slot);
                    const isSelected = formData.time === slot;

                    return (
                      <button
                        key={slot}
                        type="button"
                        disabled={isBooked}
                        onClick={() => setFormData(prev => ({ ...prev, time: slot }))}
                        className={`py-2.5 px-4 rounded-lg font-semibold text-xs transition-all duration-200 flex items-center justify-between border ${
                          isBooked
                            ? 'bg-gray-100 text-gray-400 border-gray-200 cursor-not-allowed line-through'
                            : isSelected
                            ? 'bg-blue-600 text-white border-blue-600 shadow-md ring-2 ring-blue-300 scale-105'
                            : 'bg-white text-gray-700 border-gray-200 hover:border-blue-500 hover:bg-blue-50'
                        }`}
                      >
                        <span className="flex items-center"><Clock className="h-3.5 w-3.5 mr-1.5" />{slot}</span>
                        <span className={`text-[10px] uppercase font-bold px-1.5 py-0.5 rounded ${
                          isBooked ? 'bg-gray-200 text-gray-500' : isSelected ? 'bg-blue-500 text-white' : 'bg-emerald-100 text-emerald-800'
                        }`}>
                          {isBooked ? 'Booked' : 'Available'}
                        </span>
                      </button>
                    );
                  })}
                </div>
              )}
            </div>

            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-2">Reason for Visit / Symptoms</label>
              <textarea
                value={formData.reason}
                onChange={(e) => setFormData(prev => ({ ...prev, reason: e.target.value }))}
                placeholder="Briefly describe your medical concerns or reason for visit"
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent h-24 resize-none bg-white"
                required
              />
            </div>

            <Button type="submit" className="w-full flex items-center justify-center">
              <Calendar className="h-4 w-4 mr-2" />
              Confirm & Book Appointment
            </Button>
          </form>
        </Card>
      </div>
    );
  };

  const MyAppointmentsTab = () => (
    <div>
      <h3 className="text-xl font-semibold mb-6 text-gray-900">My Medical Appointments</h3>
      
      {appointments.length === 0 ? (
        <Card className="p-8 text-center">
          <Calendar className="h-12 w-12 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-600 font-medium">No appointments booked yet.</p>
          <Button onClick={() => setActiveTab('book-appointment')} className="mt-4">
            Book Appointment Now
          </Button>
        </Card>
      ) : (
        <div className="space-y-4">
          {appointments.map(appointment => (
            <Card key={appointment.id} className="p-6">
              <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                <div>
                  <div className="flex items-center space-x-2">
                    <Stethoscope className="h-5 w-5 text-blue-600" />
                    <h4 className="text-lg font-semibold text-gray-900">{appointment.doctorName}</h4>
                  </div>
                  
                  <div className="flex flex-wrap items-center text-gray-600 mt-2 gap-4 text-sm">
                    <div className="flex items-center">
                      <Calendar className="h-4 w-4 mr-1 text-blue-500" />
                      {appointment.date}
                    </div>
                    <div className="flex items-center">
                      <Clock className="h-4 w-4 mr-1 text-blue-500" />
                      {appointment.time}
                    </div>
                  </div>
                  
                  <div className="mt-3 bg-gray-50 p-3 rounded-lg border border-gray-100">
                    <p className="text-gray-700 text-sm"><span className="font-semibold">Symptoms/Reason:</span> {appointment.reason}</p>
                    {appointment.diagnosis && (
                      <p className="text-blue-800 text-sm mt-2 font-medium bg-blue-50 p-2 rounded">
                        <span className="font-semibold">Diagnosis:</span> {appointment.diagnosis}
                      </p>
                    )}
                    {appointment.prescription && (
                      <p className="text-emerald-800 text-sm mt-2 font-medium bg-emerald-50 p-2 rounded">
                        <span className="font-semibold">Prescription:</span> {appointment.prescription}
                      </p>
                    )}
                  </div>
                </div>

                <div className="flex flex-col items-end space-y-3">
                  <span className={`px-4 py-1.5 rounded-full text-xs font-bold uppercase tracking-wider ${
                    appointment.status === 'approved' ? 'bg-emerald-100 text-emerald-800 border border-emerald-200' :
                    appointment.status === 'pending' ? 'bg-amber-100 text-amber-800 border border-amber-200' :
                    appointment.status === 'cancelled' ? 'bg-rose-100 text-rose-800 border border-rose-200' :
                    'bg-blue-100 text-blue-800 border border-blue-200'
                  }`}>
                    {appointment.status}
                  </span>

                  {(appointment.status === 'completed' || appointment.status === 'approved') && (
                    <Button
                      variant="secondary"
                      onClick={() => setSelectedPrescriptionApt(appointment)}
                      className="text-xs px-3 py-2 flex items-center shadow-sm"
                    >
                      <Printer className="h-3.5 w-3.5 mr-1 text-blue-600" />
                      Print Prescription Receipt
                    </Button>
                  )}
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );

  const ProfileMedicalHistoryTab = () => (
    <Card className="p-6">
      <h3 className="text-xl font-semibold mb-6 text-gray-900 flex items-center">
        <FileText className="h-5 w-5 text-blue-600 mr-2" />
        Patient Medical Profile & Health Records
      </h3>

      <form onSubmit={handleSaveProfile}>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Full Name"
            value={profileData.name}
            onChange={(val) => setProfileData({ ...profileData, name: val })}
            required
          />
          <Input
            label="Mobile Number"
            type="tel"
            value={profileData.mobile}
            onChange={(val) => setProfileData({ ...profileData, mobile: val })}
            required
          />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Input
            label="Age"
            type="number"
            value={profileData.age}
            onChange={(val) => setProfileData({ ...profileData, age: val })}
            required
          />

          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-2">Gender</label>
            <select
              value={profileData.gender}
              onChange={(e) => setProfileData({ ...profileData, gender: e.target.value })}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white"
            >
              <option value="male">Male</option>
              <option value="female">Female</option>
              <option value="other">Other</option>
            </select>
          </div>

          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-2">Blood Group</label>
            <select
              value={profileData.bloodGroup}
              onChange={(e) => setProfileData({ ...profileData, bloodGroup: e.target.value })}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white font-semibold text-rose-600"
            >
              {['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'].map(bg => (
                <option key={bg} value={bg}>{bg}</option>
              ))}
            </select>
          </div>
        </div>

        <Input
          label="Emergency Contact Number"
          type="tel"
          value={profileData.emergencyContact}
          onChange={(val) => setProfileData({ ...profileData, emergencyContact: val })}
          placeholder="+91 9876543210 (Guardian/Spouse)"
        />

        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700 mb-2">Known Allergies (if any)</label>
          <input
            type="text"
            value={profileData.allergies}
            onChange={(e) => setProfileData({ ...profileData, allergies: e.target.value })}
            placeholder="e.g. Penicillin, Dust, Peanuts"
            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 bg-white text-sm"
          />
        </div>

        <div className="mb-6">
          <label className="block text-sm font-medium text-gray-700 mb-2">Pre-existing Conditions & Medical History</label>
          <textarea
            value={profileData.medicalHistory}
            onChange={(e) => setProfileData({ ...profileData, medicalHistory: e.target.value })}
            placeholder="e.g. Hypertension, Diabetes, Past surgeries..."
            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 h-24 resize-none bg-white text-sm"
          />
        </div>

        <Button type="submit" className="w-full flex items-center justify-center">
          <Save className="h-4 w-4 mr-2" />
          Save & Update Medical Profile
        </Button>
      </form>
    </Card>
  );

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Printable Prescription Modal */}
      {selectedPrescriptionApt && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-2xl w-full p-8 shadow-2xl relative border border-gray-200">
            <button
              onClick={() => setSelectedPrescriptionApt(null)}
              className="absolute top-4 right-4 p-2 text-gray-400 hover:text-gray-700 rounded-lg"
            >
              <X className="h-6 w-6" />
            </button>

            {/* Receipt Content for Print */}
            <div id="printable-prescription" className="space-y-6">
              {/* Header */}
              <div className="border-b border-gray-200 pb-4 flex justify-between items-start">
                <div>
                  <div className="flex items-center space-x-2">
                    <Heart className="h-8 w-8 text-blue-600" />
                    <span className="text-2xl font-bold text-gray-900 tracking-tight">MediCare Plus</span>
                  </div>
                  <p className="text-xs text-gray-500 mt-1">Certified Healthcare & Multi-Specialty Clinic</p>
                </div>
                <div className="text-right text-xs text-gray-500">
                  <p className="font-semibold text-gray-900">Rx Receipt #{selectedPrescriptionApt.id.substring(0, 8)}</p>
                  <p>Date: {selectedPrescriptionApt.date}</p>
                </div>
              </div>

              {/* Patient & Doctor Meta */}
              <div className="grid grid-cols-2 gap-4 bg-gray-50 p-4 rounded-xl border border-gray-100 text-sm">
                <div>
                  <p className="text-xs text-gray-500 uppercase font-bold">Patient Information</p>
                  <p className="font-semibold text-gray-900 mt-1">{user.name}</p>
                  <p className="text-xs text-gray-600">Age: {user.age || 'N/A'} | Gender: {user.gender || 'N/A'}</p>
                  <p className="text-xs text-rose-600 font-semibold mt-1">Blood Group: {profileData.bloodGroup}</p>
                </div>
                <div>
                  <p className="text-xs text-gray-500 uppercase font-bold">Attending Specialist</p>
                  <p className="font-semibold text-blue-600 mt-1">{selectedPrescriptionApt.doctorName}</p>
                  <p className="text-xs text-gray-600">Consultation Time: {selectedPrescriptionApt.time}</p>
                </div>
              </div>

              {/* Medical Details */}
              <div className="space-y-4 text-sm">
                <div>
                  <p className="font-semibold text-gray-900 mb-1">Reason for Visit:</p>
                  <p className="text-gray-700 bg-white p-3 rounded-lg border border-gray-200">{selectedPrescriptionApt.reason}</p>
                </div>

                {selectedPrescriptionApt.diagnosis && (
                  <div>
                    <p className="font-semibold text-gray-900 mb-1">Medical Diagnosis:</p>
                    <p className="text-gray-800 bg-blue-50 p-3 rounded-lg border border-blue-100 font-medium">{selectedPrescriptionApt.diagnosis}</p>
                  </div>
                )}

                <div>
                  <p className="font-semibold text-gray-900 mb-1 flex items-center">
                    <Stethoscope className="h-4 w-4 mr-1 text-emerald-600" />
                    Rx Prescription & Advice:
                  </p>
                  <p className="text-emerald-900 bg-emerald-50 p-4 rounded-lg border border-emerald-200 font-medium leading-relaxed">
                    {selectedPrescriptionApt.prescription || 'No digital prescription added.'}
                  </p>
                </div>
              </div>

              {/* Footer Stamp */}
              <div className="border-t border-gray-200 pt-4 flex justify-between items-center text-xs text-gray-500">
                <p>Generated by MediCare System • Verified Medical Record</p>
                <div className="text-right">
                  <div className="w-24 border-b border-gray-400 mb-1"></div>
                  <p className="font-semibold text-gray-700">Doctor's Signature</p>
                </div>
              </div>
            </div>

            {/* Print & Download Actions */}
            <div className="mt-6 flex justify-end space-x-3">
              <Button variant="secondary" onClick={() => setSelectedPrescriptionApt(null)}>
                Close
              </Button>
              <Button
                variant="secondary"
                onClick={() => {
                  if (selectedPrescriptionApt) {
                    const text = `
==========================================================
                 MEDICARE PLUS HEALTHCARE
                OFFICIAL MEDICAL PRESCRIPTION
==========================================================
Receipt ID  : Rx-${selectedPrescriptionApt.id.substring(0, 8)}
Date        : ${selectedPrescriptionApt.date}
Time        : ${selectedPrescriptionApt.time}

PATIENT DETAILS:
----------------
Name        : ${user.name}
Age / Gender: ${user.age || 'N/A'} / ${user.gender || 'N/A'}
Blood Group : ${profileData.bloodGroup}

ATTENDING DOCTOR:
-----------------
Doctor Name : ${selectedPrescriptionApt.doctorName}

MEDICAL SUMMARY:
----------------
Reason/Symptoms : ${selectedPrescriptionApt.reason}
Diagnosis       : ${selectedPrescriptionApt.diagnosis || 'N/A'}
Prescription Rx : ${selectedPrescriptionApt.prescription || 'N/A'}

==========================================================
Verified Electronic Health Record • MediCare Plus System
==========================================================
`;
                    const blob = new Blob([text], { type: 'text/plain;charset=utf-8' });
                    const url = URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = `Prescription_Rx_${selectedPrescriptionApt.id.substring(0, 8)}.txt`;
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                    URL.revokeObjectURL(url);
                    addToast('success', 'Prescription file downloaded!');
                  }
                }}
                className="flex items-center text-xs"
              >
                <FileText className="h-4 w-4 mr-1 text-blue-600" />
                Download Rx File
              </Button>
              <Button onClick={() => window.print()} className="flex items-center text-xs">
                <Printer className="h-4 w-4 mr-1" />
                Print / Save PDF
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-2 cursor-pointer">
              <Heart className="h-8 w-8 text-blue-600" />
              <span className="text-xl font-bold text-gray-900">MediCare Patient Portal</span>
            </div>
            
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2 bg-gray-100 px-3 py-1.5 rounded-lg">
                <User className="h-5 w-5 text-blue-600" />
                <span className="text-gray-800 font-semibold text-sm">Welcome, {user.name || 'Patient'}</span>
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
          {/* Sidebar Navigation */}
          <div className="lg:w-1/4">
            <Card className="p-4">
              <nav className="space-y-2">
                <button
                  onClick={() => setActiveTab('book-appointment')}
                  className={`w-full text-left px-4 py-3 rounded-lg font-medium transition-colors flex items-center ${
                    activeTab === 'book-appointment' ? 'bg-blue-100 text-blue-700' : 'text-gray-700 hover:bg-gray-100'
                  }`}
                >
                  <Calendar className="h-4 w-4 mr-3" />
                  Book Appointment
                </button>
                <button
                  onClick={() => setActiveTab('my-appointments')}
                  className={`w-full text-left px-4 py-3 rounded-lg font-medium transition-colors flex items-center ${
                    activeTab === 'my-appointments' ? 'bg-blue-100 text-blue-700' : 'text-gray-700 hover:bg-gray-100'
                  }`}
                >
                  <Clock className="h-4 w-4 mr-3" />
                  My Appointments ({appointments.length})
                </button>
                <button
                  onClick={() => setActiveTab('medical-profile')}
                  className={`w-full text-left px-4 py-3 rounded-lg font-medium transition-colors flex items-center ${
                    activeTab === 'medical-profile' ? 'bg-blue-100 text-blue-700' : 'text-gray-700 hover:bg-gray-100'
                  }`}
                >
                  <FileText className="h-4 w-4 mr-3" />
                  Medical Profile & History
                </button>
              </nav>
            </Card>
          </div>

          {/* Main Dashboard Content */}
          <div className="lg:w-3/4">
            {activeTab === 'book-appointment' && <BookAppointmentTab />}
            {activeTab === 'my-appointments' && <MyAppointmentsTab />}
            {activeTab === 'medical-profile' && <ProfileMedicalHistoryTab />}
          </div>
        </div>
      </div>
    </div>
  );
};
