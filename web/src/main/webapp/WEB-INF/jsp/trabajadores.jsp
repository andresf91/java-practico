<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestor de Trabajadores de la Salud</title>
    <style>
        body { font-family: system-ui, sans-serif; margin: 2rem; max-width: 70rem; }
        table { border-collapse: collapse; width: 100%; margin-top: 1rem; }
        th, td { border: 1px solid #ccc; padding: .4rem .6rem; text-align: left; }
        th { background: #eee; }
        .ok { color: #14532d; background: #dcfce7; padding: .6rem; border-radius: .3rem; }
        .err { color: #7f1d1d; background: #fee2e2; padding: .6rem; border-radius: .3rem; }
        fieldset { margin-bottom: 1.5rem; }
        label { display: inline-block; width: 14rem; }
        input[type=text], input[type=date], input[type=number] { width: 22rem; }
        small { color: #555; }
    </style>
</head>
<body>

<h1>Gestor de Trabajadores de la Salud</h1>
<p>Práctico TSE 2026 — Ejercicio 1 — capa de presentación web (Servlet + JSP)</p>

<c:if test="${not empty mensaje}"><p class="ok"><c:out value="${mensaje}"/></p></c:if>
<c:if test="${not empty error}"><p class="err"><c:out value="${error}"/></p></c:if>

<form method="post" action="${pageContext.request.contextPath}/trabajadores">
    <fieldset>
        <legend>Agregar trabajador</legend>
        <p><label for="reg">Nº de registro MSP</label>
           <input id="reg" type="text" name="numeroRegistroMSP" required></p>
        <p><label for="nom">Nombre completo</label>
           <input id="nom" type="text" name="nombreCompleto" required></p>
        <p><label for="esp">Especialidad</label>
           <input id="esp" type="text" name="especialidad" required></p>
        <p><label for="fec">Fecha de alta</label>
           <input id="fec" type="date" name="fechaAlta" required></p>
        <p><label for="exp">Años de experiencia</label>
           <input id="exp" type="number" name="aniosExperiencia" min="0" max="60" value="0" required></p>
        <p><label for="pre">Prestadores (RUT)</label>
           <input id="pre" type="text" name="prestadores" placeholder="214771230011, 215558820013" required>
           <br><label></label><small>Relación TRABAJA EN, al menos uno, separados por coma.</small></p>
        <button type="submit">Agregar</button>
    </fieldset>
</form>

<form method="get" action="${pageContext.request.contextPath}/trabajadores">
    <fieldset>
        <legend>Buscar por especialidad</legend>
        <input type="text" name="especialidad" value="${especialidadBuscada}" placeholder="p. ej. Cardiología">
        <button type="submit">Buscar</button>
        <a href="${pageContext.request.contextPath}/trabajadores">Ver todos</a>
    </fieldset>
</form>

<h2>
    <c:choose>
        <c:when test="${not empty especialidadBuscada}">
            Resultados para "<c:out value="${especialidadBuscada}"/>"
        </c:when>
        <c:otherwise>Todos los trabajadores</c:otherwise>
    </c:choose>
</h2>

<table>
    <tr>
        <th>ID</th><th>Registro MSP</th><th>Nombre</th><th>Especialidad</th>
        <th>Fecha de alta</th><th>Años exp.</th><th>Trabaja en (prestadores)</th>
    </tr>
    <c:forEach var="t" items="${trabajadores}">
        <tr>
            <td>${t.id}</td>
            <td><c:out value="${t.numeroRegistroMSP}"/></td>
            <td><c:out value="${t.nombreCompleto}"/></td>
            <td><c:out value="${t.especialidad}"/></td>
            <td>${t.fechaAlta}</td>
            <td>${t.aniosExperiencia}</td>
            <td><c:out value="${t.prestadoresComoTexto}"/></td>
        </tr>
    </c:forEach>
    <c:if test="${empty trabajadores}">
        <tr><td colspan="7">Sin resultados.</td></tr>
    </c:if>
</table>

</body>
</html>
