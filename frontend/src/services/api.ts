// frontend/src/services/api.ts
import axios from 'axios';
import type {
    ExemptionRequestSummary,
    ExemptionRequestFull,
    Section,
    School,
    AddExternalCourseDTO,
    AddDocumentDTO, UE
} from '../types';

const api = axios.create({
    baseURL: '/api', // Le proxy Vite redirigera vers localhost:8080
    headers: {
        'Content-Type': 'application/json',
    },
});

export const apiService = {
    // --- DEMANDES (Requests) ---

    // Récupérer toutes les demandes de l'utilisateur
    getMyRequests: async (): Promise<ExemptionRequestSummary[]> => {
        // En attendant la sécu, on prend tout ou on filtre par email côté back si implémenté
        const response = await api.get<ExemptionRequestSummary[]>('/exemptions');
        return response.data;
    },

    // Récupérer une demande par ID
    getRequestById: async (id: string): Promise<ExemptionRequestFull> => {
        const response = await api.get<ExemptionRequestFull>(`/exemptions/${id}`);
        // Petit mapping si le backend renvoie 'globalDocuments' au lieu de 'documents'
        const data = response.data;
        return {
            ...data,
            // @ts-ignore
            documents: data.globalDocuments || data.documents || []
        };
    },

    // Créer une nouvelle demande
    createRequest: async (sectionCode: string): Promise<ExemptionRequestFull> => {
        // Le backend attend CreateExemptionRequestDto { email, sectionCode }
        const payload = {
            email: "etudiant@test.com", // EMAIL TEMPORAIRE (car pas encore de login)
            sectionCode: sectionCode
        };
        const response = await api.post<ExemptionRequestFull>('/exemptions/create', payload);
        return response.data;
    },

    // Supprimer une demande
    deleteRequest: async (id: string): Promise<void> => {
        await api.delete(`/exemptions/${id}`);
    },

    // Soumettre la demande (Changement de statut)
    submitRequest: async (id: string): Promise<void> => {
        await api.post(`/exemptions/${id}/submit`);
    },

    // --- COURS EXTERNES & DOCUMENTS ---

    // Ajouter un cours externe
    addCourse: async (requestId: string, course: AddExternalCourseDTO): Promise<void> => {
        await api.post(`/exemptions/${requestId}/courses`, course);
    },

    // Ajouter un document
    addDocument: async (requestId: string, doc: AddDocumentDTO): Promise<void> => {
        // Mapping: le front envoie {type, url}, le backend veut {type, urlStockage}
        const payload = {
            type: doc.type,
            urlStockage: doc.url,
            description: "Document ajouté par l'étudiant"
        };
        await api.post(`/exemptions/${requestId}/documents`, payload);
    },

    // --- DONNÉES DE RÉFÉRENCE (Selects) ---

    // Récupérer les sections disponibles
    getSections: async (): Promise<Section[]> => {
        // Si tu n'as pas encore l'endpoint /sections, on retourne du dur pour tester
        try {
            const response = await api.get<Section[]>('/sections');
            return response.data;
        } catch (e) {
            console.warn("API /sections non disponible, retour de données fictives");
            return [
                { code: 'INFO', label: 'Informatique de Gestion' },
                { code: 'COMPTA', label: 'Comptabilité' },
                { code: 'DROIT', label: 'Droit' },
                { code: 'MARKETING', label: 'Marketing' }
            ];
        }
    },

    // Récupérer la liste des écoles (pour l'autocomplétion)
    getSchools: async (): Promise<School[]> => {
        // Idem, endpoint fictif si pas encore prêt
        try {
            const response = await api.get<School[]>('/schools'); // Ou /kbschools
            return response.data;
        } catch (e) {
            return [
                { code: 'HE2B', etablissement: 'HE2B - ESI' },
                { code: 'EPHEC', etablissement: 'EPHEC' },
                { code: 'ULB', etablissement: 'ULB' }
            ];
        }
    },

    // Récupérer toutes les UEs (Catalogue)
    getAllUEs: async (): Promise<UE[]> => {
        const response = await api.get<UE[]>('/ue');
        return response.data;
    }
};

// On exporte 'apiService' sous le nom 'api' pour coller à tes imports dans les pages
export { apiService as api };