import React from "react";
import { Link } from "react-router-dom";

export default function Landing() {
  return (
    <div className="min-h-screen bg-slate-900 text-white flex flex-col">
      <nav className="flex justify-between items-center px-6 py-4 bg-slate-800 border-b border-slate-700 shadow-lg">
        <div className="text-2xl font-bold text-blue-400 flex items-center gap-2">
          <span>🤖</span> PayBot
        </div>
        <div className="flex gap-4">
          <Link to="/login" className="px-5 py-2 rounded-lg border border-blue-500 text-blue-400 hover:bg-blue-500 hover:text-white transition duration-200 font-semibold">
            Login
          </Link>
          <Link to="/signup" className="px-5 py-2 rounded-lg bg-blue-600 hover:bg-blue-700 text-white transition duration-200 font-semibold shadow-md shadow-blue-900/50">
            Sign Up
          </Link>
        </div>
      </nav>

      <div className="flex-1 flex flex-col items-center justify-center text-center px-4 max-w-4xl mx-auto">
        <h1 className="text-5xl md:text-6xl font-extrabold tracking-tight mb-6 bg-gradient-to-r from-blue-400 via-indigo-400 to-purple-400 bg-clip-text text-transparent">
          Welcome to Paybot
        </h1>
        <p className="text-lg md:text-xl text-slate-300 max-w-2xl mb-8 leading-relaxed">
          Your smart AI-powered assistant for fast, secure, and hassle-free mobile recharges, DTH top-ups, and utility bill payments. Experience automated payments through chatbot!
        </p>
        <Link to="/signup" className="px-8 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 hover:from-blue-600 hover:to-indigo-700 text-white rounded-xl text-lg font-bold shadow-lg transition duration-200">
          Get Started Free
        </Link>
      </div>
    </div>
  );
}