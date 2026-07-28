import React from 'react';
import { CheckCircle, AlertCircle, Info, X } from 'lucide-react';
import { ToastMessage } from '../../types';

interface ToastProps {
  toasts: ToastMessage[];
  onDismiss: (id: string) => void;
}

export const ToastContainer: React.FC<ToastProps> = ({ toasts, onDismiss }) => {
  if (!toasts || toasts.length === 0) return null;

  return (
    <div className="fixed bottom-5 right-5 z-50 flex flex-col space-y-3 max-w-md w-full px-4">
      {toasts.map((toast) => {
        const isSuccess = toast.type === 'success';
        const isError = toast.type === 'error';

        return (
          <div
            key={toast.id}
            className={`flex items-center justify-between p-4 rounded-xl shadow-xl backdrop-blur-md transition-all duration-300 transform translate-y-0 animate-bounce-short ${
              isSuccess
                ? 'bg-emerald-600 text-white'
                : isError
                ? 'bg-rose-600 text-white'
                : 'bg-blue-600 text-white'
            }`}
          >
            <div className="flex items-center space-x-3">
              {isSuccess && <CheckCircle className="h-6 w-6 flex-shrink-0" />}
              {isError && <AlertCircle className="h-6 w-6 flex-shrink-0" />}
              {!isSuccess && !isError && <Info className="h-6 w-6 flex-shrink-0" />}
              <span className="font-medium text-sm">{toast.message}</span>
            </div>
            <button
              onClick={() => onDismiss(toast.id)}
              className="ml-4 p-1 hover:bg-white/20 rounded-lg transition-colors"
            >
              <X className="h-4 w-4" />
            </button>
          </div>
        );
      })}
    </div>
  );
};
