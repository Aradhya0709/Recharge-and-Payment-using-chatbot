import { useState } from 'react';
import API from '../api/axios';

const plans = [
  { amount: 149, validity: '24 days', data: '1GB/day', description: 'Unlimited calls + 1GB/day' },
  { amount: 249, validity: '28 days', data: '1.5GB/day', description: 'Unlimited calls + 1.5GB/day' },
  { amount: 449, validity: '56 days', data: '2GB/day', description: 'Unlimited calls + 2GB/day' },
];

export default function MobileRecharge() {
  const [phoneNumber, setPhoneNumber] = useState('');
  const [operator, setOperator] = useState('');
  const [amount, setAmount] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!phoneNumber || !operator || !amount) return;
    setErrorMsg('');
    setLoading(true);

    try {
      await API.post('/recharge/mobile', {
        rechargeType: 'MOBILE_RECHARGE',
        serviceProvider: operator,
        accountNumber: phoneNumber,
        amount: parseFloat(amount),
      });
      setSuccessMsg(`Mobile recharge of ₹${amount} successful! 🎉`);
      setTimeout(() => {
        setSuccessMsg('');
        setPhoneNumber('');
        setOperator('');
        setAmount('');
      }, 3000);
    } catch (err) {
      setErrorMsg(err.response?.data?.message || 'Recharge failed. Check balance.');
      setTimeout(() => setErrorMsg(''), 4000);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6 p-4">
      <h1 className="text-2xl font-bold text-white">Mobile Recharge</h1>
      {successMsg && <div className="p-3 bg-emerald-500/20 border border-emerald-500 text-emerald-400 rounded-xl text-sm">{successMsg}</div>}
      {errorMsg && <div className="p-3 bg-red-500/20 border border-red-500 text-red-400 rounded-xl text-sm">{errorMsg}</div>}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <form onSubmit={handleSubmit} className="bg-slate-800 p-6 rounded-2xl border border-slate-700 space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Mobile Number</label>
            <input type="tel" value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} placeholder="Enter 10-digit number" className="w-full bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-sm outline-none text-white" maxLength={10} required />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Operator</label>
            <select value={operator} onChange={(e) => setOperator(e.target.value)} className="w-full bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-sm outline-none text-white" required>
              <option value="">Select Operator</option>
              <option value="JIO">Jio</option>
              <option value="AIRTEL">Airtel</option>
              <option value="VI">Vi</option>
              <option value="BSNL">BSNL</option>
            </select>
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Amount (₹)</label>
            <input type="number" value={amount} onChange={(e) => setAmount(e.target.value)} placeholder="Enter amount" className="w-full bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-sm outline-none text-white" min="10" required />
          </div>
          <button type="submit" disabled={loading} className="w-full bg-blue-600 hover:bg-blue-500 text-white p-2.5 rounded-xl font-medium text-sm disabled:opacity-50">
            {loading ? 'Processing...' : 'Recharge Now'}
          </button>
        </form>
        <div className="bg-slate-800 p-6 rounded-2xl border border-slate-700 space-y-3">
          <h2 className="text-sm font-bold text-slate-300">Popular Plans</h2>
          {plans.map((p) => (
            <button key={p.amount} type="button" onClick={() => setAmount(p.amount.toString())} className="w-full text-left p-3 rounded-xl border border-slate-700 bg-slate-700/30 hover:border-blue-500 transition-all">
              <div className="flex justify-between font-bold text-white text-sm"><span>Urgent Pack</span><span>₹{p.amount}</span></div>
              <p className="text-xs text-slate-400 mt-1">{p.description} • {p.validity}</p>
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}