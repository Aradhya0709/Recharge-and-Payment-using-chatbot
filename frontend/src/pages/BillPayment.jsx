import { useState } from 'react';
import API from '../api/axios';

const billTypes = [
  { id: 'ELECTRICITY_BILL', label: 'Electricity', icon: '⚡', gradient: 'from-yellow-500 to-orange-600', providers: [
    { value: 'ADANI_ELECTRICITY', label: 'Adani Electricity' },
    { value: 'TATA_POWER', label: 'Tata Power' },
    { value: 'BSES_RAJDHANI', label: 'BSES Rajdhani' },
    { value: 'BSES_YAMUNA', label: 'BSES Yamuna' },
    { value: 'MSEDCL', label: 'MSEDCL' },
  ]},
  { id: 'WATER_BILL', label: 'Water', icon: '💧', gradient: 'from-cyan-500 to-blue-600', providers: [
    { value: 'DELHI_JAL_BOARD', label: 'Delhi Jal Board' },
    { value: 'MUMBAI_WATER', label: 'Mumbai Water' },
    { value: 'BANGALORE_WATER', label: 'Bangalore Water' },
  ]},
  { id: 'GAS_BILL', label: 'Gas', icon: '🔥', gradient: 'from-emerald-500 to-teal-600', providers: [
    { value: 'MAHANAGAR_GAS', label: 'Mahanagar Gas' },
    { value: 'IGL', label: 'IGL' },
    { value: 'ADANI_GAS', label: 'Adani Gas' },
  ]},
  { id: 'INTERNET_BILL', label: 'Broadband', icon: '🌐', gradient: 'from-pink-500 to-rose-600', providers: [
    { value: 'ACT_FIBERNET', label: 'ACT Fibernet' },
    { value: 'JIO_FIBER', label: 'Jio Fiber' },
    { value: 'AIRTEL_XSTREAM', label: 'Airtel Xstream' },
    { value: 'BSNL_BROADBAND', label: 'BSNL Broadband' },
  ]},
];

export default function BillPayment() {
  const [selectedType, setSelectedType] = useState('ELECTRICITY_BILL');
  const [provider, setProvider] = useState('ADANI_ELECTRICITY');
  const [accountNumber, setAccountNumber] = useState('');
  const [amount, setAmount] = useState('');
  const [success, setSuccess] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const [loading, setLoading] = useState(false);

  const handleTypeChange = (typeId) => {
    setSelectedType(typeId);
    const newType = billTypes.find(b => b.id === typeId);
    if (newType?.providers?.length) setProvider(newType.providers[0].value);
  };

  const currentProviders = billTypes.find(b => b.id === selectedType)?.providers || [];

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!accountNumber || !amount) return;
    setErrorMsg('');
    setLoading(true);

    try {
      await API.post('/bills/pay', {
        billType: selectedType,
        serviceProvider: provider,
        accountNumber: accountNumber,
        amount: parseFloat(amount),
      });
      const typeLabel = billTypes.find(b => b.id === selectedType)?.label;
      setSuccess(`${typeLabel} Bill of ₹${amount} paid successfully! ✅`);
      setTimeout(() => {
        setSuccess('');
        setAccountNumber('');
        setAmount('');
      }, 3000);
    } catch (err) {
      setErrorMsg(err.response?.data?.message || 'Bill payment failed. Check balance.');
      setTimeout(() => setErrorMsg(''), 4000);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6 p-2 animate-fade-in">
      <h1 className="text-2xl font-bold text-white">Pay Utility Bills</h1>
      
      {success && (
        <div className="p-3 bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 rounded-xl text-sm">
          {success}
        </div>
      )}
      {errorMsg && (
        <div className="p-3 bg-red-500/20 text-red-400 border border-red-500/30 rounded-xl text-sm">
          {errorMsg}
        </div>
      )}

      {/* 1. Bill Type Selector Grid */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {billTypes.map((type) => (
          <button
            key={type.id}
            type="button"
            onClick={() => handleTypeChange(type.id)}
            className={`p-4 rounded-2xl border text-center transition-all duration-300 ${
              selectedType === type.id
                ? 'border-blue-500 bg-blue-600/10 text-white font-bold'
                : 'border-slate-700 bg-slate-800 text-slate-400 hover:border-slate-600'
            }`}
          >
            <div className={`w-12 h-12 mx-auto rounded-xl bg-gradient-to-br ${type.gradient} flex items-center justify-center text-xl mb-2 shadow-md`}>
              {type.icon}
            </div>
            <span className="text-sm">{type.label}</span>
          </button>
        ))}
      </div>

      {/* 2. Dynamic Payment Form */}
      <div className="bg-slate-800 p-6 rounded-2xl border border-slate-700 max-w-md mx-auto">
        <h2 className="text-base font-bold text-white mb-4">
          Enter {billTypes.find(b => b.id === selectedType)?.label} Bill Details
        </h2>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Service Provider
            </label>
            <select
              value={provider}
              onChange={(e) => setProvider(e.target.value)}
              className="w-full bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-sm text-white outline-none focus:border-blue-500 transition-all"
              required
            >
              {currentProviders.map((p) => (
                <option key={p.value} value={p.value}>{p.label}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Account / Consumer Number
            </label>
            <input 
              type="text" 
              value={accountNumber} 
              onChange={(e) => setAccountNumber(e.target.value)} 
              placeholder="e.g. 1002938122" 
              className="w-full bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-sm text-white outline-none focus:border-blue-500 transition-all" 
              required 
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              Bill Amount (₹)
            </label>
            <input 
              type="number" 
              value={amount} 
              onChange={(e) => setAmount(e.target.value)} 
              placeholder="Enter bill amount" 
              className="w-full bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-sm text-white outline-none focus:border-blue-500 transition-all" 
              min="1"
              required 
            />
          </div>

          <button 
            type="submit" 
            disabled={loading}
            className="w-full bg-blue-600 hover:bg-blue-500 text-white p-2.5 rounded-xl font-semibold text-sm shadow-lg shadow-blue-600/10 active:scale-[0.99] transition-all disabled:opacity-50"
          >
            {loading ? 'Processing...' : 'Pay Bill Securely'}
          </button>
        </form>
      </div>
    </div>
  );
}