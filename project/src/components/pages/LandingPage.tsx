import React, { useState } from 'react';
import { Calendar, Clock, Users, Stethoscope, Shield, Menu, X, LogIn, UserPlus, Phone, Mail, MapPin, Star, Heart } from 'lucide-react';
import { Button } from '../common/Button';
import { Card } from '../common/Card';

interface LandingPageProps {
  onNavigate: (page: string) => void;
}

export const LandingPage: React.FC<LandingPageProps> = ({ onNavigate }) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-teal-50">
      {/* Navigation */}
      <nav className="bg-white/80 backdrop-blur-md fixed w-full top-0 z-50 shadow-sm border-b border-gray-100">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-2 cursor-pointer" onClick={() => onNavigate('home')}>
              <Heart className="h-8 w-8 text-blue-600 animate-pulse" />
              <span className="text-xl font-bold text-gray-900 tracking-tight">MediCare Plus</span>
            </div>
            
            <div className="hidden md:flex items-center space-x-6">
              <a href="#home" className="text-gray-700 hover:text-blue-600 transition-colors font-medium">Home</a>
              <a href="#services" className="text-gray-700 hover:text-blue-600 transition-colors font-medium">Services</a>
              <a href="#doctors" className="text-gray-700 hover:text-blue-600 transition-colors font-medium">Doctors</a>
              <a href="#contact" className="text-gray-700 hover:text-blue-600 transition-colors font-medium">Contact</a>
              <Button onClick={() => onNavigate('login')} className="ml-4 flex items-center">
                <LogIn className="h-4 w-4 mr-2" />
                Login
              </Button>
            </div>

            <button
              className="md:hidden p-2 text-gray-600 hover:text-gray-900"
              onClick={() => setIsMenuOpen(!isMenuOpen)}
            >
              {isMenuOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
            </button>
          </div>
        </div>

        {/* Mobile Menu */}
        {isMenuOpen && (
          <div className="md:hidden bg-white border-t border-gray-200">
            <div className="px-4 pt-3 pb-4 space-y-2">
              <a href="#home" className="block px-3 py-2 text-gray-700 font-medium">Home</a>
              <a href="#services" className="block px-3 py-2 text-gray-700 font-medium">Services</a>
              <a href="#doctors" className="block px-3 py-2 text-gray-700 font-medium">Doctors</a>
              <a href="#contact" className="block px-3 py-2 text-gray-700 font-medium">Contact</a>
              <Button onClick={() => onNavigate('login')} className="mt-2 w-full">Login</Button>
            </div>
          </div>
        )}
      </nav>

      {/* Hero Section */}
      <section id="home" className="pt-28 pb-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <div className="text-left">
              <h1 className="text-5xl lg:text-6xl font-extrabold text-gray-900 mb-6 leading-tight tracking-tight">
                Your Health is Our
                <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-teal-600"> Priority</span>
              </h1>
              <p className="text-xl text-gray-600 mb-8 leading-relaxed">
                Book appointments with top medical specialists, manage your healthcare records online, and get reliable hospital care with MediCare Plus.
              </p>
              <div className="flex flex-col sm:flex-row gap-4">
                <Button onClick={() => onNavigate('register')} className="text-lg flex items-center justify-center">
                  <UserPlus className="h-5 w-5 mr-2" />
                  Book Appointment Now
                </Button>
                <Button variant="secondary" onClick={() => onNavigate('login')} className="text-lg">
                  Patient Portal
                </Button>
              </div>
            </div>
            
            <div className="relative">
              <div className="relative z-10 animate-float">
                <div className="bg-gradient-to-br from-blue-100 via-teal-50 to-blue-50 rounded-3xl p-8 transform rotate-1 hover:rotate-0 transition-transform duration-500 shadow-lg">
                  <div className="bg-white rounded-2xl p-6 shadow-xl">
                    <div className="flex items-center mb-4">
                      <div className="w-12 h-12 bg-blue-600 rounded-full flex items-center justify-center shadow-md">
                        <Stethoscope className="h-6 w-6 text-white" />
                      </div>
                      <div className="ml-4">
                        <h3 className="font-semibold text-gray-900 text-lg">Dr. Om Jain</h3>
                        <p className="text-gray-600 text-sm">Senior Cardiologist</p>
                      </div>
                    </div>
                    <div className="space-y-3">
                      <div className="flex items-center text-sm text-gray-600">
                        <Calendar className="h-4 w-4 mr-2 text-blue-600" />
                        Available Mon - Sat
                      </div>
                      <div className="flex items-center text-sm text-gray-600">
                        <Clock className="h-4 w-4 mr-2 text-blue-600" />
                        10:00 AM - 6:00 PM
                      </div>
                      <div className="flex items-center text-sm text-amber-600 font-medium">
                        <Star className="h-4 w-4 mr-1 fill-amber-400 text-amber-400" />
                        4.9 Rating (120+ Reviews)
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Services Section */}
      <section id="services" className="py-16 bg-white border-t border-gray-100">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-4xl font-bold text-gray-900 mb-4">Our Core Services</h2>
            <p className="text-xl text-gray-600">Comprehensive healthcare solutions tailored for your well-being</p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            {[
              { icon: Calendar, title: 'Instant Booking', desc: 'Book appointments with specialists in real-time.' },
              { icon: Users, title: 'Expert Doctors', desc: 'Access to verified doctors across all medical fields.' },
              { icon: Shield, title: 'Secure Records', desc: 'Your medical histories and records remain private and protected.' }
            ].map((service, index) => (
              <Card key={index} className="p-8 text-center hover:bg-blue-50/50 transition-colors">
                <div className="w-16 h-16 bg-gradient-to-br from-blue-600 to-teal-600 rounded-2xl flex items-center justify-center mx-auto mb-6 shadow-md transform hover:scale-110 transition-transform duration-300">
                  <service.icon className="h-8 w-8 text-white" />
                </div>
                <h3 className="text-xl font-semibold mb-3 text-gray-900">{service.title}</h3>
                <p className="text-gray-600 leading-relaxed">{service.desc}</p>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer id="contact" className="bg-gray-900 text-white py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid md:grid-cols-3 gap-8">
            <div>
              <div className="flex items-center space-x-2 mb-4">
                <Heart className="h-8 w-8 text-blue-400" />
                <span className="text-xl font-bold">MediCare Plus</span>
              </div>
              <p className="text-gray-400">Your trusted partner in healthcare management and online booking.</p>
            </div>
            <div>
              <h4 className="text-lg font-semibold mb-4 text-white">Contact Info</h4>
              <div className="space-y-3 text-gray-400">
                <div className="flex items-center"><Phone className="h-4 w-4 mr-2 text-blue-400" />+91 9876543210</div>
                <div className="flex items-center"><Mail className="h-4 w-4 mr-2 text-blue-400" />info@medicareplus.com</div>
                <div className="flex items-center"><MapPin className="h-4 w-4 mr-2 text-blue-400" />Mumbai, Maharashtra</div>
              </div>
            </div>
            <div>
              <h4 className="text-lg font-semibold mb-4 text-white">Quick Links</h4>
              <div className="space-y-2">
                <button onClick={() => onNavigate('login')} className="block text-gray-400 hover:text-white transition-colors">Patient Login</button>
                <button onClick={() => onNavigate('doctor-signup')} className="block text-gray-400 hover:text-white transition-colors">Doctor Registration</button>
                <button onClick={() => onNavigate('register')} className="block text-gray-400 hover:text-white transition-colors">New Patient Signup</button>
              </div>
            </div>
          </div>
          <div className="border-t border-gray-800 pt-8 mt-8 text-center text-gray-400 text-sm">
            <p>&copy; {new Date().getFullYear()} MediCare Plus. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
};
