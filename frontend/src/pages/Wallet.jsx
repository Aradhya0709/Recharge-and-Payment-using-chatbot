import { useState, useEffect } from 'react';
import API from '../api/axios';

export default function Wallet() {
  const [balance, setBalance] = useState(0);
  const [amount, setAmount] = useState('');
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchBalance();
  }, []);

  const fetchBalance = async () => {
    try {
      const res = await API.get('/wallet/balance');
      setBalance(res.data.data.balance || 0);
    } catch (err) {
      console.log('Balance fetch error:', err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAddMoney = async (e) => {
    e.preventDefault();
    setError('');
    if (!amount || parseFloat(amount) <= 0) return;
    
    try {
      await API.post('/wallet/add-money', { amount: parseFloat(amount) });
      setSuccess(`₹${amount} added successfully! 💰`);
      setAmount('');
      fetchBalance(); // Refresh balance from backend
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to add money. Try again.');
      setTimeout(() => setError(''), 3000);
    }
  };

  return (
    <div className="max-w-md mx-auto bg-slate-800 p-6 rounded-2xl border border-slate-700 space-y-6">
      <h1 className="text-xl font-bold text-white">My Wallet</h1>
      
      {/* Dynamic Success Alert Pop */}
      {success && (
        <div className="p-3 bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 rounded-xl text-xs animate-fade-in">
          {success}
        </div>
      )}
      {error && (
        <div className="p-3 bg-red-500/20 text-red-400 border border-red-500/30 rounded-xl text-xs animate-fade-in">
          {error}
        </div>
      )}
      
      {/* Wallet Balance Board */}
      <div className="p-4 bg-slate-700/40 border border-slate-700 rounded-xl">
        <p className="text-xs text-slate-400 font-medium">Available Balance</p>
        <p className="text-2xl font-extrabold text-white mt-1">
          ₹{loading ? '---' : Number(balance).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
        </p>
      </div>
      
      {/* Money Loader Form */}
      <form onSubmit={handleAddMoney} className="space-y-4">
        <div>
          <input 
            type="number" 
            value={amount} 
            onChange={(e) => setAmount(e.target.value)} 
            placeholder="Enter Amount to Add" 
            className="w-full bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-sm text-white outline-none focus:border-blue-500 transition-all" 
            min="1"
            required 
          />
        </div>
        <button 
          type="submit" 
          className="w-full bg-blue-600 hover:bg-blue-500 text-white p-2.5 rounded-xl text-sm font-semibold shadow-lg shadow-blue-600/10 active:scale-[0.99] transition-all"
        >
          Add Money
        </button>
      </form>
    </div>
  );
}