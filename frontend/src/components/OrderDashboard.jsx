import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { FaSync, FaReceipt } from 'react-icons/fa';
import OrderStatusBadge from './OrderStatusBadge';

export default function OrderDashboard({ refreshTrigger }) {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchOrders = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/orders');
      // Sort by ID descending (newest first)
      const sorted = response.data.sort((a, b) => b.id - a.id);
      setOrders(sorted);
      setError(null);
    } catch (err) {
      console.error('Error fetching orders:', err);
      setError('Could not connect to Order Service. Make sure backend is running.');
    } finally {
      setLoading(false);
    }
  };

  // Initial load and when refreshTrigger changes
  useEffect(() => {
    fetchOrders();
  }, [refreshTrigger]);

  // Polling every 3 seconds for real-time visual step updates
  useEffect(() => {
    const interval = setInterval(() => {
      fetchOrders();
    }, 3000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="bg-white rounded-xl shadow-lg border border-gray-100 overflow-hidden">
      <div className="p-6 border-b border-gray-100 flex justify-between items-center bg-gray-50">
        <h2 className="text-2xl font-bold text-gray-800 flex items-center">
          <FaReceipt className="mr-3 text-indigo-600" />
          Live Orders
        </h2>
        <button 
          onClick={fetchOrders}
          className="text-gray-500 hover:text-indigo-600 transition-colors p-2 rounded-full hover:bg-indigo-50"
          title="Refresh Orders"
        >
          <FaSync className={loading ? "animate-spin" : ""} />
        </button>
      </div>

      {error ? (
        <div className="p-8 text-center text-red-500 bg-red-50">
          <p className="font-medium">{error}</p>
        </div>
      ) : orders.length === 0 ? (
        <div className="p-12 text-center text-gray-500">
          <FaReceipt className="mx-auto text-4xl mb-4 text-gray-300" />
          <p className="text-lg">No orders found.</p>
          <p className="text-sm">Place an order to see it appear here.</p>
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Order ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Customer</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Item</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Amount</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {orders.map((order) => (
                <tr key={order.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    #{order.id}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700 font-medium">
                    {order.customerName}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {order.item}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-medium">
                    ${order.amount.toFixed(2)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <OrderStatusBadge status={order.status} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
