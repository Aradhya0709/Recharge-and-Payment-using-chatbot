import { useState } from 'react';
import API from '../api/axios';

export default function Chatbot() {
  const [isOpen, setIsOpen] = useState(false);
  const [input, setInput] = useState('');
  
  // Default welcome message block
  const defaultMessage = [
    { text: "Hello! How can I assist you with your payments today? 🤖", isBot: true }
  ];
  
  const [messages, setMessages] = useState(defaultMessage);
  const [loading, setLoading] = useState(false);

  // 🚪 Chat Close and Clean Handler Function
  const handleCloseChat = () => {
    setIsOpen(false);
    setMessages(defaultMessage); // ✅ Chat band hote hi history clear aur reset ho jayegi!
    setInput('');
  };

  const handleSend = async (e) => {
    e.preventDefault();
    if (!input.trim() || loading) return;

    const userMsg = input;
    setMessages(prev => [...prev, { text: userMsg, isBot: false }]);
    setInput('');
    setLoading(true);

    try {
      const res = await API.post('/chatbot/query', { message: userMsg });
      const reply = res.data.data.reply || "I couldn't understand that. Try asking about balance, transactions, or recharges!";
      setMessages(prev => [...prev, { text: reply, isBot: true }]);
    } catch (err) {
      setMessages(prev => [...prev, { text: "Sorry, something went wrong. Please try again! 😔", isBot: true }]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed bottom-6 right-6 z-50 font-sans">
      
      {/* FLOATING ACTION BUBBLE */}
      {!isOpen && (
        <button
          onClick={() => setIsOpen(true)}
          className="w-14 h-14 rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 shadow-xl shadow-blue-500/30 flex items-center justify-center text-2xl hover:scale-110 active:scale-95 transition-all cursor-pointer"
        >
          🤖
        </button>
      )}

      {/* ACTIVE CHAT FRAME BOX */}
      {isOpen && (
        <div className="w-80 sm:w-96 h-[450px] bg-slate-800 border border-slate-700 rounded-2xl shadow-2xl flex flex-col overflow-hidden animate-fade-in">
          
          {/* Top Panel Brand Bar */}
          <div className="bg-gradient-to-r from-blue-600 to-indigo-600 p-4 flex justify-between items-center text-white">
            <div className="flex items-center gap-2">
              <span className="text-xl">🤖</span>
              <div>
                <h3 className="text-sm font-bold">PayBot AI Assistant</h3>
                <p className="text-[10px] text-blue-200">Online & Active</p>
              </div>
            </div>
            {/* ✅ Trigger close clean handler on click cross button */}
            <button onClick={handleCloseChat} className="text-white/80 hover:text-white text-xl font-bold p-1">×</button>
          </div>

          {/* Chat Messaging Box Viewport */}
          <div className="flex-1 p-4 overflow-y-auto space-y-3 bg-slate-900/40 flex flex-col">
            {messages.map((m, idx) => (
              <div key={idx} className={`flex ${m.isBot ? 'justify-start' : 'justify-end'}`}>
                <div className={`max-w-[80%] p-3 rounded-2xl text-xs whitespace-pre-line leading-relaxed ${
                  m.isBot 
                    ? 'bg-slate-700 text-slate-100 rounded-tl-none border border-slate-600/60' 
                    : 'bg-blue-600 text-white rounded-tr-none shadow-md'
                }`}>
                  {m.text}
                </div>
              </div>
            ))}
            {loading && (
              <div className="flex justify-start">
                <div className="bg-slate-700 text-slate-300 rounded-2xl rounded-tl-none border border-slate-600/60 p-3 text-xs">
                  Thinking...
                </div>
              </div>
            )}
          </div>

          {/* Text Box Messaging Footer Form */}
          <form onSubmit={handleSend} className="p-3 bg-slate-800 border-t border-slate-700 flex gap-2">
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Ask: What is my wallet balance?"
              className="flex-1 bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-xs text-white outline-none focus:border-blue-500 transition-all"
            />
            <button type="submit" disabled={loading} className="bg-blue-600 hover:bg-blue-500 text-white px-4 py-2 rounded-xl text-xs font-bold transition-all shadow-md active:scale-95 disabled:opacity-50">
              Send
            </button>
          </form>
        </div>
      )}
    </div>
  );
}