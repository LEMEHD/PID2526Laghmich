import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../services/api';
import { Link } from 'react-router-dom';
import { Plus, FileText, Calendar, AlertCircle, Trash2 } from 'lucide-react';
import type { StatutDemande } from '../types';
// CORRECTION : On importe le type d'erreur spécifique à Axios
import { AxiosError } from 'axios';

const getStatusBadge = (status: StatutDemande) => {
    const styles: Record<string, string> = {
        DRAFT: 'bg-gray-100 text-gray-800 border-gray-200',
        SUBMITTED: 'bg-blue-50 text-blue-700 border-blue-200',
        UNDER_REVIEW: 'bg-yellow-50 text-yellow-700 border-yellow-200',
        NEED_INFO: 'bg-orange-50 text-orange-700 border-orange-200',
        APPROVED: 'bg-green-50 text-green-700 border-green-200',
        PARTIALLY_APPROVED: 'bg-teal-50 text-teal-700 border-teal-200',
        REJECTED: 'bg-red-50 text-red-700 border-red-200',
    };

    const labels: Record<string, string> = {
        DRAFT: 'Brouillon',
        SUBMITTED: 'Soumis',
        UNDER_REVIEW: 'En cours',
        NEED_INFO: 'Info requise',
        APPROVED: 'Approuvé',
        PARTIALLY_APPROVED: 'Partiellement Approuvé',
        REJECTED: 'Rejeté',
    };

    return (
        <span className={`px-3 py-1 rounded-full text-xs font-medium border ${styles[status] || styles.DRAFT}`}>
            {labels[status] || status}
        </span>
    );
};

export const Dashboard = () => {
    const queryClient = useQueryClient();

    const { data: requests, isLoading, isError } = useQuery({
        queryKey: ['myRequests'],
        queryFn: api.getMyRequests
    });

    // Mutation pour supprimer
    const deleteMutation = useMutation({
        mutationFn: api.deleteRequest,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['myRequests'] });
        },
        // CORRECTION : Typage strict de l'erreur
        onError: (error: AxiosError<{ error: string }>) => {
            // On vérifie si le backend a renvoyé un message spécifique, sinon message par défaut
            const msg = error.response?.data?.error || "Impossible de supprimer ce dossier.";
            alert("Erreur : " + msg);
        }
    });

    const handleDelete = (e: React.MouseEvent, id: string) => {
        e.preventDefault(); // Empêche d'entrer dans la fiche (le Link)
        e.stopPropagation();

        if (confirm("Êtes-vous sûr de vouloir supprimer ce dossier définitivement ?")) {
            deleteMutation.mutate(id);
        }
    };

    if (isLoading) return (
        <div className="flex justify-center p-12">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
        </div>
    );

    if (isError) return (
        <div className="bg-red-50 p-4 rounded-lg flex items-center text-red-700 border border-red-200">
            <AlertCircle className="w-5 h-5 mr-2" />
            Impossible de charger vos demandes. Vérifiez le backend.
        </div>
    );

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <div>
                    <h1 className="text-2xl font-bold text-gray-900">Mes Demandes</h1>
                    <p className="text-gray-500 mt-1">Gérez vos dossiers de dispenses</p>
                </div>
                <Link
                    to="/new"
                    className="inline-flex items-center px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition shadow-sm font-medium"
                >
                    <Plus className="w-4 h-4 mr-2" />
                    Nouvelle Demande
                </Link>
            </div>

            {requests?.length === 0 && (
                <div className="text-center py-12 bg-white rounded-xl shadow-sm border border-gray-200">
                    <div className="bg-indigo-50 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                        <FileText className="w-8 h-8 text-indigo-600" />
                    </div>
                    <h3 className="text-lg font-medium text-gray-900">Aucune demande</h3>
                    <p className="text-gray-500 mt-1">Commencez par créer votre premier dossier.</p>
                </div>
            )}

            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {requests?.map((req) => (
                    <Link
                        key={req.id}
                        to={`/request/${req.id}`}
                        className="block bg-white p-6 rounded-xl shadow-sm border border-gray-200 hover:shadow-md hover:border-indigo-300 transition group relative"
                    >
                        <div className="flex justify-between items-start mb-4">
                            {getStatusBadge(req.statut)}
                            <span className="text-xs text-gray-400 font-mono">#{String(req.id).substring(0, 8)}</span>
                        </div>

                        <h3 className="font-semibold text-lg text-gray-900 group-hover:text-indigo-600 transition pr-8">
                            Section {req.section}
                        </h3>

                        <div className="mt-4 pt-4 border-t border-gray-100 flex items-center text-sm text-gray-500">
                            <Calendar className="w-4 h-4 mr-2" />
                            {new Date(req.createdAt).toLocaleDateString('fr-FR', {
                                day: 'numeric', month: 'long', year: 'numeric'
                            })}
                        </div>

                        {/* --- BOUTON SUPPRIMER --- */}
                        {(() => {
                            const isDraft = req.statut === 'DRAFT';
                            return (
                                <button
                                    /* Si c'est pas un brouillon, on empêche le clic (et la propagation vers le Link) */
                                    onClick={(e) => {
                                        e.preventDefault();
                                        e.stopPropagation();
                                        if (isDraft) handleDelete(e, req.id);
                                    }}
                                    disabled={!isDraft}
                                    className={`absolute bottom-4 right-4 p-2 rounded-full shadow-sm border transition-all z-10 
                    ${isDraft
                                        ? 'bg-white border-gray-200 text-gray-400 hover:text-red-600 hover:bg-red-50 hover:border-red-200 cursor-pointer group/delete'
                                        : 'bg-gray-50 border-gray-100 text-gray-300 cursor-not-allowed'
                                    }`}
                                    title={isDraft ? "Supprimer ce dossier" : "Impossible de supprimer une demande soumise"}
                                >
                                    <Trash2 className={`w-5 h-5 transition-transform ${isDraft ? 'group-hover/delete:scale-110' : ''}`} />
                                </button>
                            );
                        })()}
                    </Link>
                ))}
            </div>
        </div>
    );
};