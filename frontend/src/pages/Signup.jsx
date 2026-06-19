import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Signup() {
  const { signup, loading } = useAuth();
  const navigate = useNavigate();
  
  const [form, setForm] = useState({
    fullName: '',
    email: '',
    phone: '',
    gender: '',
    address: '',
    password: '',
    confirmPassword: '',
  });
  
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!form.fullName || !form.email || !form.phone || !form.password) {
      setError('Please fill in all required fields');
      return;
    }

    if (form.password.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }

    if (form.password !== form.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    // 🟢 KEY FIX: Backend accepts both "fullname" (@JsonProperty) and "fullName" (field name)
    const signupData = {
      fullName: form.fullName,
      email: form.email,
      phone: form.phone,
      password: form.password
    };
    
    const result = await signup(signupData);
    if (result.success) {
      setSuccess('Account created successfully! Redirecting to login...');
      setTimeout(() => navigate('/login'), 2000);
    } else {
      setError(result.message || 'Signup failed');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-900 px-4 py-8">
      <div className="w-full max-w-md bg-slate-800 border border-slate-700 p-8 rounded-2xl shadow-xl">
        
        <div className="text-center mb-6">
          <div className="inline-flex w-14 h-14 rounded-xl bg-gradient-to-br from-blue-500 to-indigo-500 items-center justify-center mb-3 font-bold text-xl text-white">
            PB
          </div>
          <h1 className="text-2xl font-bold text-white">Create Account</h1>
          <p className="text-slate-400 text-sm">Join PayBot for smart payments</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {error && <div className="p-3 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-xs">{error}</div>}
          {success && <div className="p-3 rounded-lg bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs">{success}</div>}

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Full Name *</label>
            <input
              type="text"
              name="fullName"
              value={form.fullName}
              onChange={handleChange}
              placeholder="Enter your full name"
              className="w-full bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-sm text-white focus:border-blue-500 outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Email Address *</label>
            <input
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              placeholder="Enter your email"
              className="w-full bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-sm text-white focus:border-blue-500 outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Phone Number *</label>
            <input
              type="tel"
              name="phone"
              value={form.phone}
              onChange={handleChange}
              placeholder="Enter phone number"
              className="w-full bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-sm text-white focus:border-blue-500 outline-none"
              maxLength={10}
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Password *</label>
            <input
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              placeholder="Minimum 6 characters"
              className="w-full bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-sm text-white focus:border-blue-500 outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Confirm Password *</label>
            <input
              type="password"
              name="confirmPassword"
              value={form.confirmPassword}
              onChange={handleChange}
              placeholder="Re-enter password"
              className="w-full bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-sm text-white focus:border-blue-500 outline-none"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 hover:bg-blue-500 text-white font-medium p-2.5 rounded-xl transition-all text-sm mt-2 disabled:opacity-50"
          >
            {loading ? 'Registering...' : 'Create Account'}
          </button>
        </form>

        <div className="mt-4 text-center">
          <p className="text-xs text-slate-400">
            Already have an account?{' '}
            <Link to="/login" className="text-blue-400 hover:underline font-medium">
              Sign In
            </Link>
          </p>
        </div>

      </div>
    </div>
  );
}