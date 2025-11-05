<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Compte courants</title>

    <!-- ✅ Style moderne coherent avec aside -->
    <style>
        body {
            font-family: "Inter", sans-serif;
            margin: 0;
            background: #fafafa;
            color: #222;
        }

        .main-content {
            margin-left: 220px; /* coherent avec aside */
            padding: 30px;
        }

        h1 {
            font-size: 26px;
            font-weight: 600;
            margin-bottom: 20px;
            color: #111;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        }

        th {
            background: #f1f1f1;
            padding: 14px;
            text-align: left;
            font-weight: 600;
            color: #333;
            border-bottom: 1px solid #e5e5e5;
            font-size: 15px;
        }

        td {
            padding: 12px;
            border-bottom: 1px solid #eee;
            font-size: 14px;
        }

        tr:hover {
            background: #f7f7f7;
        }

        /* ✅ Bouton moderne */
        a.view-btn {
            padding: 7px 14px;
            background: #222;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 500;
            transition: 0.2s;
        }

        a.view-btn:hover {
            background: #000;
        }

    </style>
</head>

<body>
    <%@ include file="aside.jsp" %>

    <div class="main-content">
        <h1>Liste des Comptes Courants</h1>

        <table>
            <thead>
                <tr>
                    <th>Id</th>
                    <th>Client</th>
                    <th>Solde Initial</th>
                    <th>Date de creation</th>
                    <th>Actions</th>
                </tr>
            </thead>

            <tbody>
                <c:forEach var="c" items="${comptesCourants}">
                    <tr>
                        <td>${c.id}</td>
                        <td>${c.idClient}</td>
                        <td>${c.soldeInitial}</td>
                        <td>${c.dateCreation}</td>
                        <td>
                            <a class="view-btn" href="compte-courant-details?id=${c.id}">
                                Voir details
                            </a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

</body>
</html>
