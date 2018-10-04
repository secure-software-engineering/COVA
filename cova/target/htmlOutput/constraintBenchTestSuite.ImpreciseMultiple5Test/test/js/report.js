$(function () { 
	if ($("#code")[0])
		window.Split(['.navigation','#code'], {
			sizes: [10, 90],
			minSize:[5,window.innerWidth/2]
		});
	
	$('#tree').jstree({
		"types" : {
			"default" : {
				"icon": "fas fa-box"
			},
			"file" : {
				"icon" : "fab fa-java"
			},
			"noSource": {
				"icon": "fas fa-question",
				"color": "red"
			}
		},
		"plugins" : [ "types" ]
	}).bind("select_node.jstree",function (e, data) {
		window.location = $("#"+data.node.id+"_anchor").attr('href'); 
	});	
 });