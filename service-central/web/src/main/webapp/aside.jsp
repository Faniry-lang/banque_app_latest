<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="https://unpkg.com/@phosphor-icons/web"></script>

<style>

    .sidebar {
        display: flex;
        flex-direction: column;
        height: 100%;
        width: 220px;
        position: fixed;
        top: 0;
        left: 0;
        background: #f5f5f5;
        border-right: 1px solid #dedede;
        padding-top: 25px;
        padding-bottom: 25px; 
        font-family: "Inter", sans-serif;
        box-shadow: 2px 0 6px rgba(0,0,0,0.05);
    }

    .sidebar-header {
        text-align: left;
        font-size: 22px;
        font-weight: 700;
        color: #333;
        margin-bottom: 20px;
        padding-left: 20px; /* aligné avec les liens */
    }

    .sidebar-menu {
        flex-grow: 1;
    }

    .sidebar a {
        padding: 12px 20px;
        margin: 6px 0; /* supprimer margin horizontal pour alignement */
        font-size: 15px;
        color: #333;
        text-decoration: none;
        display: flex;
        align-items: center;
        border-radius: 8px;
        transition: all 0.2s ease-in-out;
        font-weight: 500;
        gap: 14px;
    }

    .sidebar a:hover {
        background: #bdbdbdff;
        color: #000;
    }

    .sidebar a i {
        font-size: 20px;
        color: #111;
    }

    .sidebar-footer {
        margin-left: 10px;
        margin-right: 10px;
        margin-top: 20px; 
        padding-bottom: 25px; 
    }

    .logout-button {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 10px 15px;
        background-color: #acacacff;
        margin-bottom: 25px;
        color: white;
        text-decoration: none;
        border-radius: 10px;
        font-size: 14px;
        font-weight: 500;
        gap: 8px;
        transition: opacity 0.2s, background 0.2s;
    }

    .logout-button:hover {
        background-color: #111;
        opacity: 0.9;
    }

    .main-content {
        margin-left: 220px;
        padding: 20px;
        font-family: "Inter", sans-serif;
    }
</style>

<div class="sidebar">
    <div class="sidebar-header">Banking</div>

    <div class="sidebar-menu">
        <a href="#"><i class="ph ph-house"></i>Accueil</a>
        <a href="/service-central-web/comptes-courants"><i class="ph ph-credit-card"></i>Liste des Comptes Courants</a>
        <a href="/service-central-web/devises"><i class="ph ph-coins"></i>Gestion des Devises</a>
        <a href="/service-central-web/taux-variation"><i class="ph ph-chart-line"></i>Taux de Variation</a>
        <a href="/service-central-web/plafonds"><i class="ph ph-arrow-up"></i>Plafonds</a>
        <a href="/service-central-web/frais-bancaire"><i class="ph ph-money"></i>Plafonds</a>
    </div>

    <div class="sidebar-footer">
        <a href="/service-central-web/logout" class="logout-button"><i class="ph ph-sign-out"></i>Se déconnecter</a>
    </div>
</div>
