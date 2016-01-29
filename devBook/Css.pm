package DevSeminar::Css;

sub GetCss {

	return q{
	<style>
	textarea {
		margin:0px;
		border:1px;
		border-right:0px;
		border-color:#eceff5;
		padding:0px;
	}
	input#NEWRESOURCENAME {
		border:1px;
		border-right:0px;
		margin:0px;
		padding:0px;
		border-color:#eceff5;
	}
	
	i {
		color:#3b5998;
	}
	div.FOOTERQUOTE {
		filter:alpha(opacity=55);
		width:100%;
		position:fixed;
		text-align:center;
		font-style:italic;
		color:#3b5998;
		bottom:0px;
		height:25px;
	}
	div.APPNAME {
		width:99%;
		position:fixed;
		left:4px;
		top:4px;
		color:#3b5998;
		font-family:modern;
		font-size:32px;
		background-color:none;
		height:32px;
	}
	div.RESOURCELINK {
		float:left;
	}
	div.SHOWLIKERS {
		float:bottom;
	}
	img.LOADING {
		float:right;
		height:32px;
		width:32px;
		filter:alpha(opacity=60);
		display:none;
	} 
	a.THISPAGE{
		cursor:hand;
		text-decoration:none;
		color:#3b5998
	}
	b.LIKECOMMENTUSER {
		color:#3b5998
	}
	a.THISPAGE:hover{
		text-decoration:underline;
	}
	a.THISPAGE:visited {
		text-decoration:none;
		color:#3b5998
	}
	
	.LCMENULIKE {
		float:left;
		width:12.5%;
	}
	
	.LIKECOMMENT {
		background-color:#eceff5;
		border-width: 1px;
		border-style: solid;
		border-color: white;
	}
	.LCBLOCKCOMMENT, .LCMENU{
		border-width:1px;
		border-style:solid;
		border-color:white;
	}
	div.LCCOMMENTBOX {
		display:none;
	}
	img.DELETE {
		float:right;
		cursor:hand;
	}	
	img.EDIT {
		float:right;
		filter: alpha(opacity:75);
		cursor:hand;
		height:18px;
		width:18px;
	}
	div#RESOURCE {
		float:right;
		background-color:#eceff5;
		width:100%;
		border-width: 1px;
		border-left:2px;
		border-style: solid;
		border-color: white;
	}
	div#SEMINAR {
		border-width: 1px;
		border-right:2px;
		border-style: solid;
		border-color: white;
		
		background-color:#eceff5; /*#E0E0F8;*/
		width:100%;
	}
	div#PAGE {
		width:100%;
		float:center;
		color:#000414;
	}
	img.HEADING {
		float:left;
		background-color:none;
		height:90px;
		width:90px;
		float:left;
		filter:alpha(opacity=75);
	}
	input#NEWRESOURCENAME {
		width:100%;
	}
	hr {
		width: 100%
		color:white;
	}
	div.NEWRESOURCE {
		display:none;
	}
	div.EXISTINGRESOURCE {
	}
	div.RIGHT {
		width:50%;
		float:right;
	}
	div.LEFT {
		width:49.85%;
		float:left;
	}
	.RESOURCEHELP {
		color:#3b5998;
		width: 30%;
		font-weight:bold;
		float:left;
	}
	button.SAVE {
		float:right;
		width:10%;
	}
	button.CANCEL {
		float:right;
		width:10%;
	}
	h3.THISPAGE {
		background-color:#eceff5;
		border:none;
		
	}
	p.AUTHOR {
		float:right;
		font-weight:bold;	
	}
	table.NAVIGATION {
		text-align:center;
		width:40%
	}
	td.NAV {
		width:25%;
	}
	img.NAVLEFT {
		filter:alpha(opacity=50);
		border:0px;
		height:32px;
		width:32px;
	}

	img.NAVRIGHT {
		filter:alpha(opacity=50);
		border:0px;
		height:32px;
		width:32px;
	}

/*	img.NAVRIGHTHIGH {
		border:0px;
		height:32px;
		filter:alpha(opacity=100); 
		width:45px;
	}
	img.NAVLEFTHIGH  {
		border:0px;
		height:32px;
		filter:alpha(opacity=100);
		width:45px;
	}*/
	img.HIGH {
		filter:alpha(opacity=100);
		width:45px;
		cursor:hand;
	}
	hr {
		color:white;
	}
	.RESOURCEFULUSER {
		color:#3b5998;
		font-size:14px;
	}
	
</style>	
	};
}

1;
