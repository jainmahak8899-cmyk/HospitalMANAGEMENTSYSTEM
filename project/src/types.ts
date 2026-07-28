export interface User {
  id: string;
  name: string;
  email: string;
  role: 'patient' | 'doctor' | 'admin';
  age?: number;
  gender?: string;
  mobile?: string;
  specialization?: string;
  experience?: string;
  bloodGroup?: string;
  allergies?: string;
  medicalHistory?: string;
  emergencyContact?: string;
}

export interface Appointment {
  id: string;
  patientId: string;
  doctorId: string;
  patientName: string;
  doctorName: string;
  date: string;
  time: string;
  reason: string;
  status: 'pending' | 'approved' | 'cancelled' | 'completed';
  createdAt: string;
  diagnosis?: string;
  prescription?: string;
}

export interface Doctor {
  id: string;
  name: string;
  specialization: string;
  experience: string;
  email: string;
  mobile: string;
  hospital?: string;
  qualifications?: string;
  consultationFee?: number;
}

export interface ToastMessage {
  id: string;
  type: 'success' | 'error' | 'info';
  message: string;
}
