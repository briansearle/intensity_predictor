<%--
  Created by IntelliJ IDEA.
  User: nicksh
  Date: 6/23/2015
  Time: 1:57 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>PREGO Peptide Response Predictor</title>
  </head>
  <body>
Using data independent acquisition to model high-responding peptides for targeted proteomics experiments.<br>
Brian C. Searle, Jarrett D. Egertson, James G. Bollinger, Andrew B. Stergachis and Michael J. MacCoss<br>
Published by Molecular and Cellular Proteomics on June 22, 2015, doi: 10.1074/mcp.M115.051300<br>
<br>
PREGO is a software tool that predicts high responding peptides for SRM experiments. PREGO predicts peptide responses with an artificial neural network trained using 11 minimally redundant, maximally relevant properties. Crucial to its success, PREGO is trained using fragment ion intensities of equimolar synthetic peptides extracted from data independent acquisition (DIA) experiments.<br>
<br>
<form action="results.jsp" method="POST">
  List of peptides:<br>
  <textarea name="peptides" rows="25" cols="25">

  </textarea><br>
<input type="submit">
</form>
  </body>
</html>
