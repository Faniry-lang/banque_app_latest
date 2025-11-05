<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Details du Compte Courant</title>

    <!-- Lucide Icons CDN -->
    <script src="https://unpkg.com/lucide@latest"></script>

    <style>
        body { font-family: sans-serif; margin: 0; background-color: #f4f4f4; }
        .container { margin: 20px; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        h1, h2 { color: #333; border-bottom: 2px solid #eee; padding-bottom: 10px; }

        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #f8f8f8; }

        .actions { margin-top: 20px; display: flex; gap: 10px; }
        .btn {
            background-color: #222;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
        }
        .btn:hover { background-color: #000; }
        .btn-danger { background-color: #dc3545; }
        .btn-danger:hover { background-color: #c82333; }

        button svg { width: 16px; height: 16px; stroke-width: 2.2; }

        /* Modals */
        .modal { display: none; position: fixed; z-index: 20; left: 0; top: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.4); }
        .modal-content { background-color: #fff; margin: 8% auto; padding: 20px; border-radius: 8px; max-width: 500px; }
        .close { cursor: pointer; float: right; font-size: 26px; color: #666; }
        .close:hover { color: #000; }

        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; }
        .form-group input, .form-group select { width: 95%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; }
    </style>
</head>

<body>
    <%@ include file="aside.jsp" %>
    <div class="main-content">
    <div class="container">
        <h1>Détails du Compte Courant</h1>
        <c:if test="${not empty compte}">
            <table class="details">
                <tr><th>ID du Compte</th><td>${compte.id}</td></tr>
                <tr><th>ID du Client</th><td>${compte.idClient}</td></tr>
                <tr><th>Solde Actuel</th><td><b>${solde}</b></td></tr>
                <tr><th>Date de Création</th><td>${compte.dateCreation}</td></tr>
            </table>
        </c:if>

        <div class="actions">
            <button class="btn" onclick="openModal('transactionModal')"><i data-lucide="receipt"></i> Nouvelle Transaction</button>
            <button class="btn" onclick="openModal('virementModal')"><i data-lucide="send"></i> Nouveau Virement</button>
        </div>

        <h2>Virements</h2>
        <table>
            <thead>
                <tr><th>ID</th><th>Date</th><th>Montant</th><th>Frais</th><th>Bénéficiaire (ID)</th><th>Actions</th></tr>
            </thead>
            <tbody>
                <c:forEach var="virement" items="${virements}">
                    <tr>
                        <td>${virement.id}</td>
                        <td>${virement.dateVirement}</td>
                        <td>${virement.montant}</td>
                        <td>${virement.totalFrais}</td>
                        <td>${virement.idCompteBeneficiaire}</td>
                        <td>
                            <form action="compte-courant-details" method="post" style="margin:0;">
                                <input type="hidden" name="action" value="valider_virement">
                                <input type="hidden" name="idCompte" value="${compte.id}">
                                <input type="hidden" name="virementId" value="${virement.id}">
                                <button type="submit" class="btn"><i data-lucide="check-circle"></i> Valider</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty virements}"><tr><td colspan="6">Aucun virement pour ce compte.</td></tr></c:if>
            </tbody>
        </table>

        <h2>Transactions</h2>
        <table>
            <thead>
                <tr><th>ID</th><th>Date</th><th>Type</th><th>Montant</th><th>Lié au Virement (ID)</th><th>Actions</th></tr>
            </thead>
            <tbody>
                <c:forEach var="tx" items="${transactions}">
                    <tr>
                        <td>${tx.id}</td>
                        <td>${tx.dateTransaction}</td>
                        <td>${tx.typeTransactionStr}</td>
                        <td>${tx.montant}</td>
                        <td>${tx.idVirementSource != null ? tx.idVirementSource : "N/A"}</td>
                        <td>
                             <form action="compte-courant-details" method="post" style="margin:0;">
                                <input type="hidden" name="action" value="valider_transaction">
                                <input type="hidden" name="idCompte" value="${compte.id}">
                                <input type="hidden" name="transactionId" value="${tx.id}">
                                <button type="submit" class="btn"><i data-lucide="check-circle"></i> Valider</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty transactions}"><tr><td colspan="6">Aucune transaction pour ce compte.</td></tr></c:if>
            </tbody>
        </table>
        <br/>
        <a href="comptes-courants">Retour à la liste</a>
    </div>

    <!-- MODALS -->
    <div id="transactionModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeModal('transactionModal')">&times;</span>
            <h2>Nouvelle Transaction</h2>
            <form action="compte-courant-details" method="POST">
                <input type="hidden" name="action" value="insert_transaction">
                <input type="hidden" name="idCompte" value="${compte.id}">
                <div class="form-group"><label for="montantTx">Montant</label><input type="number" id="montantTx" name="montant" step="0.01" required></div>
                <div class="form-group"><label for="typeTx">Type</label><select id="typeTx" name="idTypeTransaction" required><c:forEach var="type" items="${typesTransactions}"><option value="${type.id}">${type.nom}</option></c:forEach></select></div>
                <div class="form-group">
                    <label for="deviseTx">Devise</label>
                    <select id="deviseTx" name="nomDevise" required>
                        <c:forEach var="nom" items="${devisesNoms}">
                            <option value="${nom}">${nom}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group"><label for="dateTx">Date</label><input type="date" id="dateTx" name="dateTransaction" value="<%= java.time.LocalDate.now() %>" required></div>
                <button type="submit" class="btn">Valider</button>
            </form>
        </div>
    </div>

    <div id="virementModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeModal('virementModal')">&times;</span>
            <h2>Nouveau Virement</h2>
            <form action="compte-courant-details" method="POST">
                <input type="hidden" name="action" value="insert_virement">
                <input type="hidden" name="idCompte" value="${compte.id}">
                <div class="form-group"><label for="beneficiaireVir">Compte Bénéficiaire</label><select id="beneficiaireVir" name="idCompteBeneficiaire" required><c:forEach var="c" items="${comptes}"><c:if test="${c.id != compte.id}"><option value="${c.id}">Compte N°${c.id} (Client N°${c.idClient})</option></c:if></c:forEach></select></div>
                <div class="form-group"><label for="montantVir">Montant</label><input type="number" id="montantVir" name="montant" step="0.01" required></div>
                <div class="form-group">
                    <label for="deviseVir">Devise</label>
                    <select id="deviseVir" name="nomDevise" required>
                        <c:forEach var="nom" items="${devisesNoms}">
                            <option value="${nom}">${nom}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group"><label for="dateVir">Date</label><input type="date" id="dateVir" name="dateVirement" value="<%= java.time.LocalDate.now() %>" required></div>
                <button type="submit" class="btn">Valider</button>
            </form>
        </div>
    </div>

    <div id="errorModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeModal('errorModal')">&times;</span>
            <h2>Une erreur est survenue</h2>
            <p id="errorMessage" style="color: red; word-wrap: break-word;"></p>
            <button class="btn btn-danger" onclick="closeModal('errorModal')"><i data-lucide="alert-octagon"></i> Fermer</button>
        </div>
    </div>

    <script>
        lucide.createIcons();
        function openModal(modalId) { document.getElementById(modalId).style.display = "block"; }
        function closeModal(modalId) { document.getElementById(modalId).style.display = "none"; }
        window.onclick = function(event) { if (event.target.classList.contains('modal')) { event.target.style.display = "none"; } }
        document.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            const errorMsg = urlParams.get('error');
            if (errorMsg) {
                document.getElementById('errorMessage').textContent = decodeURIComponent(errorMsg);
                openModal('errorModal');
            }
        });
    </script>
    </div>
</body>
</html>
