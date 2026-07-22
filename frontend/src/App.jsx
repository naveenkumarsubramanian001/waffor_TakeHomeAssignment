import React, { useState } from 'react';
import OrderForm from './components/OrderForm';
import OrderDashboard from './components/OrderDashboard';
import { FaHamburger } from 'react-icons/fa';

function App() {
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const handleOrderCreated = () => {
    // Increment to trigger a re-fetch in the dashboard
    setRefreshTrigger(prev => prev + 1);
  };

  return (
    <div className="min-h-screen bg-gray-100 py-10 px-4 sm:px-6 lg:px-8">
      <div className="max-w-5xl mx-auto">
        <header className="mb-10 text-center">
          <div className="inline-flex items-center justify-center p-4 bg-indigo-600 rounded-full mb-4 shadow-lg">
            <FaHamburger className="text-4xl text-white" />
          </div>
          <h1 className="text-4xl font-extrabold text-gray-900 tracking-tight">
            Waffor Food Ordering System
          </h1>
          <p className="mt-2 text-lg text-gray-600">
            Microservices Architecture with Camunda BPMN & ActiveMQ
          </p>
        </header>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-1">
            <OrderForm onOrderCreated={handleOrderCreated} />
            
            <div className="bg-white rounded-xl shadow-lg p-6 border border-gray-100 mt-8">
              <h3 className="text-lg font-bold text-gray-800 mb-4">Architecture Flow</h3>
              <ul className="space-y-3 text-sm text-gray-600">
                <li className="flex items-start"><span className="text-indigo-600 mr-2 font-bold">1.</span> Order Placed (REST)</li>
                <li className="flex items-start"><span className="text-indigo-600 mr-2 font-bold">2.</span> Event Published to ActiveMQ</li>
                <li className="flex items-start"><span className="text-indigo-600 mr-2 font-bold">3.</span> Camunda Workflow Starts</li>
                <li className="flex items-start"><span className="text-indigo-600 mr-2 font-bold">4.</span> Payment Processed (80% success)</li>
                <li className="flex items-start"><span className="text-indigo-600 mr-2 font-bold">5.</span> Kitchen Prepares Food</li>
                <li className="flex items-start"><span className="text-indigo-600 mr-2 font-bold">6.</span> Delivery Assigned</li>
                <li className="flex items-start"><span className="text-indigo-600 mr-2 font-bold">7.</span> Order Delivered</li>
              </ul>
            </div>
          </div>
          
          <div className="lg:col-span-2">
            <OrderDashboard refreshTrigger={refreshTrigger} />
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
