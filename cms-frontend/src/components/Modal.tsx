import React, { ReactNode } from "react";
import "../styles/Modal.css";

interface ModalProps {
	isOpen: boolean;
	onClose: () => void;
	title: string;
	children: ReactNode;
}

const Modal: React.FC<ModalProps> = ({ isOpen, onClose, title, children }) => {
	if (!isOpen) {
		return null;
	}
	const handleContentClick = (e: React.MouseEvent) => {
		e.stopPropagation();
	};

	return (
		<div
			className="modal-overlay"
			onClick={onClose}
		>
			<div
				className="modal-content"
				onClick={handleContentClick}
			>
				<div className="modal-header">
					<h2>{title}</h2>
					<button
						className="modal-close-button"
						onClick={onClose}
					>
						&times;
					</button>
				</div>
				<div className="modal-body">
					{children}
				</div>
			</div>
		</div>
	);
};

export default Modal;
