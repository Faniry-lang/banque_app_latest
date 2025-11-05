<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="itu.banque.api.dtos.FraisBancaireDto" %>
<!DOCTYPE html>
<html>
<head>
    <title>Gestion des Frais Bancaires</title>

    <script src="https://unpkg.com/lucide@latest"></script>

    <style>
        body {
            margin: 0;
            font-family: sans-serif;
            background-color: #f4f4f4;
        }

        .main-content {
            margin-left: 200px;
            padding: 20px;
        }

        h1, h2 {
            color: #333;
            border-bottom: 2px solid #eee;
            padding-bottom: 10px;
        }

        .form-container {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 1px 4px rgba(0,0,0,0.1);
            margin-bottom: 25px;
        }

        .form-group {
            margin-bottom: 12px;
        }

        .form-group label {
            display: block;
            font-size: 14px;
            color: #555;
            margin-bottom: 6px;
        }

        .form-group input {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 15px;
            background-color: #fafafa;
        }

        .btn {
            padding: 10px 16px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            color: white;
            font-size: 14px;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }

        .btn svg { width: 18px; height: 18px; stroke-width: 2.2; }

        .btn-primary { background-color: #222; }
        .btn-primary:hover { background-color: #000; }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 1px 4px rgba(0,0,0,0.1);
        }

        th {
            background: #fafafa;
            text-align: left;
            padding: 12px;
            font-weight: 600;
            color: #444;
        }

        td {
            padding: 12px;
            border-top: 1px solid #eee;
        }

        tr:nth-child(even) {
            background-color: #fcfcfc;
        }

        td input {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 6px;
        }

        .modal {
            display: none;
            position: fixed;
            z-index: 20;
            left: 0; top: 0;
            width: 100%; height: 100%;
            background-color: rgba(0,0,0,0.4);
            justify-content: center;
            align-items: center;
        }

        .modal-content {
            background: white;
            padding: 20px;
            width: 90%;
            max-width: 450px;
            border-radius: 10px;
            position: relative;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }

        .close-button {
            position: absolute;
            right: 15px;
            top: 10px;
            font-size: 26px;
            cursor: pointer;
            color: #777;
        }

        .close-button:hover { color: #000; }
    </style>
</head>
<body>

<%@ include file="aside.jsp" %>

<div class="main-content">

    <h1>Gestion des Frais Bancaires</h1>

    <!-- AJOUTER -->
    <div class="form-container">
        <h2>Ajouter un frais bancaire</h2>

        <form action="frais-bancaire" method="post">

            <div class="form-group">
                <label>Montant inférieur :</label>
                <input type="number" step="0.01" name="montantInf" required>
            </div>

            <div class="form-group">
                <label>Montant supérieur :</label>
                <input type="number" step="0.01" name="montantSup" required>
            </div>

            <div class="form-group">
                <label>Frais forfaitaire :</label>
                <input type="number" step="0.01" name="fraisForfaitaire">
            </div>

            <div class="form-group">
                <label>Frais en pourcentage :</label>
                <input type="number" step="0.01" name="fraisPourcentage">
            </div>

            <div class="form-group">
                <label>Date du frais :</label>
                <input type="date" name="dateFrais">
            </div>

            <button type="submit" class="btn btn-primary">
                <i data-lucide="plus-circle"></i> Ajouter
            </button>
        </form>
    </div>

    <h2>Liste des Frais Bancaires</h2>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Montant Inf</th>
            <th>Montant Sup</th>
            <th>Frais Forfaitaire</th>
            <th>Frais %</th>
            <th>Date Frais</th>
        </tr>
        </thead>

        <tbody>
        <%
            List<FraisBancaireDto> frais = (List<FraisBancaireDto>) request.getAttribute("fraisBancaires");
            if (frais != null) {
                for (FraisBancaireDto f : frais) {
        %>
        <tr>
            <td><%= f.getId() %></td>
            <td><%= f.getMontantInf() %></td>
            <td><%= f.getMontantSup() %></td>
            <td><%= f.getFraisForfaitaire() != null ? f.getFraisForfaitaire() : "-" %></td>
            <td><%= f.getFraisPourcentage() %></td>
            <td><%= f.getDateFrais() %></td>
        </tr>
        <%
                }
            }
        %>
        </tbody>
    </table>

</div>

<!-- MODAL ERREUR -->
<div id="errorModal" class="modal">
    <div class="modal-content">
        <span class="close-button">&times;</span>
        <h2>Erreur</h2>
        <p id="errorMessage"></p>
    </div>
</div>

<script>
    lucide.createIcons();

    window.onload = function() {
        const urlParams = new URLSearchParams(window.location.search);
        const error = urlParams.get('error');
        if (error) {
            document.getElementById('errorMessage').innerText = decodeURIComponent(error);
            document.getElementById('errorModal').style.display = 'flex';
        }

        const modal = document.getElementById('errorModal');
        const closeButton = document.querySelector('.close-button');

        closeButton.onclick = closeModal;
        window.onclick = (e) => { if (e.target == modal) closeModal() };

        function closeModal() {
            modal.style.display = 'none';
            const newUrl = window.location.pathname;
            window.history.replaceState({}, '', newUrl);
        }
    }
</script>

</body>
</html>
