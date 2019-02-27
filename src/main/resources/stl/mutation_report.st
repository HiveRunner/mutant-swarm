\<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

\<html xmlns="http://www.w3.org/1999/xhtml">
\<head>
  \<title>Mutation Report\</title>
  \<link rel="stylesheet" type="text/css" href="<cssFile>"/>
\</head>

\<body onload="loadMutationCountBar()">
  \<img src="mutant-swarm-logo.png"/>

  \<h1><results.suiteName>\</h1>
  
  \<h2>Run at: <results.startTime>\</h2>
  
  \<h2 class="small_margin_padding">Mutants killed:\</h2>
  \<div class="bg_survived">
    \<div id="mutationCountBar" class="bg_killed" style="width:0%;"> 0%
    \</div>
  \</div>

  <scripts:{ script |

  \<h3><script.fileName>\</h3>
  \<table class="table">
    <script.statements:{ statement |
      <statement.lines:{ line |
      \<tr>
        <if(line.survivors)>
        \<td class="tooltip bg_survived line_number">
        <elseif(line.killed)>
        \<td class="tooltip bg_killed line_number">
        <else>
        \<td class="line_number">
        <endif>
            \<span>
              <line.number>
            \</span>
          <if(line.killed||line.survivors)>
            \<span class="tooltiptext" id="popupLineNum">
              Genes: <line.geneCount>\<br/>
              Mutations: <line.mutationCount>\<br/>
              <if(line.survivors)>
              \<span class="fg_survived">
              <endif>
              Survived: <line.survivorCount>\<br/>
              <if(line.survivors)>
              \</span>
              <endif>
              <if(line.killed&&!line.survivors)>
              \<span class="fg_killed">
              <endif>
              Killed: <line.killedCount>
              <if(line.killed&&!line.survivors)>
              \</span>
              <endif>
            \</span>
          <endif>
        \</td>
        \<td>
          \<p>
          <line.elements:{ element |
            <if(element.survivors)>
              \<span class="popup bg_survived" onclick="togglePopUp('<script.index>_<element.startIndex>')">
            <elseif(element.killed)>
              \<span class="popup bg_killed" onclick="togglePopUp('<script.index>_<element.startIndex>')">
            <else>
              \<span>
            <endif>
                \<span class="sqlcode" id="code_block_<script.index>_<element.startIndex>"><element.chars>\</span>
              <if(element.killed||element.survivors)>
                \<span class="popuptext" id="popup_<script.index>_<element.startIndex>">
                <if(element.survivors)>
                  \<span class="popuptexttitle">Survivors:\</span>\<br/>
                  <element.survivors:{outcome |
                    <outcome.mutant.mutator.description> in
                      <outcome.testOutcomes: { testOutcome |'<testOutcome.testName>'}; separator=",">
                  }; separator="\<br/>">
                <endif>
                <if(element.killed)>
                  <if(element.survivors)>
                  \<br/>
                    <endif>
                  \<span class="popuptexttitle">Killed:\</span>\<br/>
                  <element.killed:{ outcome |
                    <outcome.mutant.mutator.description>
                  }; separator="\<br/>">
                <endif>
                \<br/>
                \</span>
              <endif>
              \</span>
          }>
          \</p>
        \</td>

      \</tr>
      }>
    }>

\</table>
}>

\<script type="text/javascript">
  function togglePopUp(popUpId) {
    var popup = document.getElementById('popup_' + popUpId);
    popup.classList.toggle("show");
  \}
  
  function loadMutationCountBar() {
      var elem = document.getElementById("mutationCountBar");   
      var width = 0;
      var id = setInterval(frame, 10);
      function frame() {
        if (width >= <mutantsKilledPercentage>) {
            clearInterval(id);
        \} else {
            width++; 
            elem.style.width = width + '%'; 
            elem.innerHTML = width * 1  + '%';
        \}
      \}
  \}
\</script>


\</body>

\</html>
