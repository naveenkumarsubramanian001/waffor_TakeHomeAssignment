import React from 'react';
import { FaCheckCircle as FaCheckCircleOk, FaTimesCircle as FaTimesCircleErr, FaClock as FaClockWait, FaTruck as FaTruckRide, FaUtensils as FaUtensilsPrep, FaCreditCard as FaCreditCardPay } from 'react-icons/fa';

export default function OrderStatusBadge({ status }) {
  const getBadgeStyle = () => {
    switch (status) {
      case 'PLACED':
        return { bg: 'bg-gray-100', text: 'text-gray-800', icon: <FaClockWait className="mr-1.5" />, label: 'Placed' };
      case 'PAYMENT_PROCESSING':
        return { bg: 'bg-blue-100', text: 'text-blue-800', icon: <FaCreditCardPay className="mr-1.5" />, label: 'Payment' };
      case 'KITCHEN_PREP':
        return { bg: 'bg-yellow-100', text: 'text-yellow-800', icon: <FaUtensilsPrep className="mr-1.5" />, label: 'Kitchen Prep' };
      case 'OUT_FOR_DELIVERY':
        return { bg: 'bg-purple-100', text: 'text-purple-800', icon: <FaTruckRide className="mr-1.5" />, label: 'Out for Delivery' };
      case 'DELIVERED':
        return { bg: 'bg-green-100', text: 'text-green-800', icon: <FaCheckCircleOk className="mr-1.5" />, label: 'Delivered' };
      case 'CANCELLED':
        return { bg: 'bg-red-100', text: 'text-red-800', icon: <FaTimesCircleErr className="mr-1.5" />, label: 'Cancelled' };
      default:
        return { bg: 'bg-gray-100', text: 'text-gray-800', icon: null, label: status };
    }
  };

  const style = getBadgeStyle();

  return (
    <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium ${style.bg} ${style.text}`}>
      {style.icon}
      {style.label}
    </span>
  );
}
