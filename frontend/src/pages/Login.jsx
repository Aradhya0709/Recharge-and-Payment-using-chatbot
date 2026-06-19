import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const { login, loading } = useAuth();
  const navigate = useNavigate();
  
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!email || !password) {
      setError('Please enter both Email and Password');
      return;
    }

    // Both email and password parameters verified cleanly
    const result = await login(email, password);
    if (result.success) {
      navigate('/dashboard');
    } else {
      setError(result.message || 'Invalid email or password');
    }
  };

  return (
    /* ✅ absolute inset alignment to lock it perfectly in the center of the screen */
    <div className="fixed inset-0 min-h-screen w-screen flex items-center justify-center bg-slate-900 px-4 z-50 overflow-y-auto">
      <div className="w-full max-w-md bg-slate-800 border border-slate-700 p-8 rounded-2xl shadow-2xl block my-auto">
        
        {/* Logo & Header */}
        <div className="text-center mb-8">
          <div className="inline-flex w-14 h-14 rounded-xl bg-gradient-to-br from-blue-500 to-indigo-500 items-center justify-center mb-3 font-bold text-xl text-white shadow-lg shadow-blue-500/20">
            PB
          </div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Welcome Back</h1>
          <p className="text-slate-400 text-sm">Sign in to your PayBot account</p>
        </div>

        {/* Form Processing */}
        <form onSubmit={handleSubmit} className="space-y-5">
          {error && (
            <div className="p-3 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 text-xs">
              {error}
            </div>
          )}

          {/* Email Box Input */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5 tracking-wide">Email Address</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="name@gmail.com"
              className="w-full bg-slate-700/40 border border-slate-600/80 rounded-xl p-3 text-sm text-white placeholder-slate-500 focus:border-blue-500 focus:bg-slate-700/80 outline-none transition-all"
              required
            />
          </div>

          {/* Password Box Input */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5 tracking-wide">Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              className="w-full bg-slate-700/40 border border-slate-600/80 rounded-xl p-3 text-sm text-white placeholder-slate-500 focus:border-blue-500 focus:bg-slate-700/80 outline-none transition-all"
              required
            />
          </div>

          {/* Login Submit Trigger */}
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 hover:bg-blue-500 text-white font-semibold p-3 rounded-xl transition-all text-sm mt-4 shadow-lg shadow-blue-600/10 active:scale-[0.99] disabled:opacity-50"
          >
            {loading ? 'Verifying Credentials...' : 'Sign In with Email'}
          </button>
        </form>

        <div className="mt-6 text-center border-t border-slate-700/60 pt-4">
          <p className="text-xs text-slate-400">
            Don't have an account?{' '}
            <Link to="/signup" className="text-blue-400 hover:text-blue-300 font-semibold transition-colors">
              Create Account
            </Link>
          </p>
        </div>

      </div>
    </div>
  );
}