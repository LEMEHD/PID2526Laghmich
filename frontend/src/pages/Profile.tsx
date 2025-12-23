// MODIFICATION : J'ai retiré "Book" de la liste car inutile ici
import { User, Mail, School } from 'lucide-react';
import { Link } from 'react-router-dom';

export const Profile = () => {
    return (
        <div className="max-w-3xl mx-auto space-y-6">
            <div className="flex justify-between items-center">
                <h1 className="text-2xl font-bold text-gray-900">Mon Profil</h1>
                <Link to="/" className="text-indigo-600 hover:text-indigo-800 font-medium">Retour</Link>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                {/* Bandeau décoratif */}
                <div className="bg-gradient-to-r from-indigo-600 to-blue-500 h-32"></div>

                <div className="px-8 pb-8">
                    <div className="relative flex items-end -mt-12 mb-6">
                        <div className="bg-white p-2 rounded-full shadow-md">
                            <div className="bg-gray-100 w-24 h-24 rounded-full flex items-center justify-center text-gray-400">
                                <User className="w-12 h-12" />
                            </div>
                        </div>
                        <div className="ml-4 mb-2">
                            <h2 className="text-2xl font-bold text-gray-900">Étudiant Test</h2>
                            {/* MODIFICATION : Utilisation de Mail ici */}
                            <div className="flex items-center text-gray-500 mt-1">
                                <Mail className="w-4 h-4 mr-1.5" />
                                <span>etudiant@isfce.be</span>
                            </div>
                        </div>
                    </div>

                    <div className="grid gap-8 md:grid-cols-2">
                        {/* Colonne Gauche */}
                        <div className="space-y-4">
                            <h3 className="font-semibold text-gray-900 flex items-center border-b border-gray-100 pb-2">
                                <User className="w-4 h-4 mr-2 text-indigo-600" />
                                Informations Personnelles
                            </h3>
                            <div className="grid grid-cols-3 gap-4 text-sm">
                                <div className="text-gray-500 col-span-1">Prénom</div>
                                <div className="col-span-2 font-medium">Jean</div>

                                <div className="text-gray-500 col-span-1">Nom</div>
                                <div className="col-span-2 font-medium">Dupont</div>

                                <div className="text-gray-500 col-span-1">Matricule</div>
                                <div className="col-span-2 font-mono bg-gray-50 inline-block px-2 rounded">2024-5892</div>
                            </div>
                        </div>

                        {/* Colonne Droite */}
                        <div className="space-y-4">
                            <h3 className="font-semibold text-gray-900 flex items-center border-b border-gray-100 pb-2">
                                <School className="w-4 h-4 mr-2 text-indigo-600" />
                                Scolarité
                            </h3>
                            <div className="space-y-4 text-sm">
                                <div>
                                    <div className="text-gray-500 mb-1">Section actuelle</div>
                                    <div className="font-medium text-gray-900 bg-indigo-50 p-2 rounded-lg border border-indigo-100">
                                        Bachelier en Informatique de Gestion
                                    </div>
                                </div>
                                <div className="flex justify-between items-center">
                                    <span className="text-gray-500">Année académique</span>
                                    <span className="font-bold text-indigo-600">2025-2026</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};