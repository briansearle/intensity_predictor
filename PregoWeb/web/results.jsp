<%@ page import="edu.washington.maccoss.intensity_predictor.ClassifyPeptides" %>
<%@ page import="edu.washington.maccoss.intensity_predictor.structures.PeptideData" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: nicksh
  Date: 6/23/2015
  Time: 2:03 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<%String[] peptideParam = request.getParameter("peptides").split("\\s+");
  List<PeptideData> results = ClassifyPeptides.getGoodPeptides(Arrays.asList(peptideParam));
for (PeptideData peptide : results) {
  %><%=peptide.toString()%><br><%
}
%>
</body>
</html>
