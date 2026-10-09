import React, { useState, useEffect } from 'react';
import { Calendar, Clock, Heart, User, Stethoscope, CheckCircle, XCircle, FileText } from 'lucide-react';
import { User as UserType, Appointment } from '../../types';
import { Button } from '../common/Button';
import { Card } from '../common/Card';

interface DoctorDashboardProps {
  user: UserType;
  onLogout: () => void;
  addToast: (type: 'success' | 'error' | 'info', message: string) => void;
}

export const DoctorDashboard: React.FC<DoctorDashboardProps> = ({ user, onLogout, addToast }) => {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [prescriptionText, setPrescriptionText] = useState<{ [id: string]: string }>({});
  const [diagnosisText, setDiagnosisText] = useState<{ [id: string]: string }>({});

  useEffect(() => {
    const savedAppointments = localStorage.getItem('appointments');
    if (savedAppointments) {
      const allAppointments = JSON.parse(savedAppointments);
      setAppointments(allAppointments.filter((apt: Appointment) => apt.doctorId === user.id));
    }
  }, [user.id]);

  const updateAppointmentStatus = (appointmentId: string, status: 'approved' | 'cancelled' | 'completed') => {
    const savedAppointments = localStorage.getItem('appointments');
    if (savedAppointments) {
      const allAppointments = JSON.parse(savedAppointments);
      const updatedAppointments = allAppointments.map((apt: Appointment) => {
        if (apt.id === appointmentId) {
          const updated = { ...apt, status };
          if (prescriptionText[appointmentId]) {
            updated.prescription = prescriptionText[appointmentId];
          }
          if (diagnosisText[appointmentId]) {
            updated.diagnosis = diagnosisText[appointmentId];
          }
          return updated;
        }
        return apt;
      });

      localStorage.setItem('appointments', JSON.stringify(updatedAppointments));
      setAppointments(updatedAppointments.filter((apt: Appointment) => apt.doctorId === user.id));
      addToast('success', `Appointment status updated to ${status}`);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-2">
              <Heart className="h-8 w-8 text-blue-600" />
              <span className="text-xl font-bold text-gray-900">MediCare Specialist Portal</span>
            </div>
            
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2 bg-teal-50 px-3 py-1.5 rounded-lg border border-teal-100">
                <Stethoscope className="h-5 w-5 text-teal-600" />
                <span className="text-gray-900 font-semibold text-sm">Dr. {user.name || 'Doctor'}</span>
              </div>
              <Button onClick={onLogout} variant="secondary">
                Logout
              </Button>
            </div>
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Patient Booking Requests</h1>
          <p className="text-gray-600 mt-1">Manage, diagnose, and prescribe medicine for your patient schedule</p>
        </div>

        {appointments.length === 0 ? (
          <Card className="p-8 text-center">
            <Calendar className="h-12 w-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600 font-medium">No patient appointments scheduled yet.</p>
          </Card>
        ) : (
          <div className="space-y-6">
            {appointments.map(appointment => (
              <Card key={appointment.id} className="p-6">
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
                  <div className="flex-1">
                    <div className="flex items-center space-x-4 mb-3">
                      <div className="w-12 h-12 bg-blue-600 rounded-full flex items-center justify-center shadow-md">
                        <User className="h-6 w-6 text-white" />
                      </div>
                      <div>
                        <h3 className="text-lg font-semibold text-gray-900">{appointment.patientName}</h3>
                        <div className="flex items-center text-gray-600 space-x-4 text-sm mt-1">
                          <div className="flex items-center">
                            <Calendar className="h-4 w-4 mr-1 text-blue-500" />
                            {appointment.date}
                          </div>
                          <div className="flex items-center">
                            <Clock className="h-4 w-4 mr-1 text-blue-500" />
                            {appointment.time}
                          </div>
                        </div>
                      </div>
                    </div>
                    
                    <div className="bg-gray-50 rounded-lg p-4 border border-gray-100 space-y-3">
                      <div>
                        <h4 className="font-semibold text-gray-900 text-xs uppercase tracking-wide">Symptoms / Reason:</h4>
                        <p className="text-gray-700 text-sm mt-1">{appointment.reason}</p>
                      </div>

                      {(appointment.status === 'approved' || appointment.status === 'completed') && (
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-3 pt-2 border-t border-gray-200">
                          <div>
                            <label className="block text-xs font-semibold text-gray-700 mb-1 flex items-center">
                              <FileText className="h-3.5 w-3.5 text-blue-600 mr-1" />
                              Diagnosis:
                            </label>
                            <input
                              type="text"
                              value={diagnosisText[appointment.id] ?? appointment.diagnosis ?? ''}
                              onChange={(e) => setDiagnosisText({ ...diagnosisText, [appointment.id]: e.target.value })}
                              placeholder="e.g. Mild Hypertension"
                              className="w-full text-xs p-2 border border-gray-300 rounded focus:ring-1 focus:ring-blue-500 bg-white"
                            />
                          </div>

                          <div>
                            <label className="block text-xs font-semibold text-gray-700 mb-1 flex items-center">
                              <Stethoscope className="h-3.5 w-3.5 text-emerald-600 mr-1" />
                              Rx Prescription & Advice:
                            </label>
                            <input
                              type="text"
                              value={prescriptionText[appointment.id] ?? appointment.prescription ?? ''}
                              onChange={(e) => setPrescriptionText({ ...prescriptionText, [appointment.id]: e.target.value })}
                              placeholder="e.g. Paracetamol 500mg, Rest"
                              className="w-full text-xs p-2 border border-gray-300 rounded focus:ring-1 focus:ring-blue-500 bg-white"
                            />
                          </div>
                        </div>
                      )}
                    </div>
                  </div>
                  
                  <div className="flex flex-col items-end space-y-3">
                    <span className={`px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider ${
                      appointment.status === 'approved' ? 'bg-emerald-100 text-emerald-800' :
                      appointment.status === 'pending' ? 'bg-amber-100 text-amber-800' :
                      appointment.status === 'cancelled' ? 'bg-rose-100 text-rose-800' :
                      'bg-blue-100 text-blue-800'
                    }`}>
                      {appointment.status}
                    </span>
                    
                    {appointment.status === 'pending' && (
                      <div className="flex space-x-2">
                        <Button
                          onClick={() => updateAppointmentStatus(appointment.id, 'approved')}
                          variant="success"
                          className="text-xs px-3 py-2 flex items-center"
                        >
                          <CheckCircle className="h-3.5 w-3.5 mr-1" />
                          Approve
                        </Button>
                        <Button
                          onClick={() => updateAppointmentStatus(appointment.id, 'cancelled')}
                          variant="danger"
                          className="text-xs px-3 py-2 flex items-center"
                        >
                          <XCircle className="h-3.5 w-3.5 mr-1" />
                          Reject
                        </Button>
                      </div>
                    )}

                    {appointment.status === 'approved' && (
                      <Button
                        onClick={() => updateAppointmentStatus(appointment.id, 'completed')}
                        variant="primary"
                        className="text-xs px-3 py-2 flex items-center"
                      >
                        <CheckCircle className="h-3.5 w-3.5 mr-1" />
                        Save Notes & Complete
                      </Button>
                    )}
                  </div>
                </div>
              </Card>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
