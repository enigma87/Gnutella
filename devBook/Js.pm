use lib "/home/kkishore/p4/intranet/";
package DevSeminar::Js;
use DevSeminar;
use A****Utils;

sub GetJs {
	my $dbh = shift;

	my $seminarinfo = DevSeminar::GetRelevantSeminarInfo($dbh, {
		CURRENTDATE => A****Today("YYYYMMDD"), 
		WHICH => 'CLOSEST',
	});

	my $seminarjsobj = PerlObjectToJScript($seminarinfo);
	my $lastseminar = SQLValues(qq{
		select 
			to_char(max(expecteddate), 'YYYYMMDD') 
		from 
			booktransaction 
		where 
			deleted is null 
			and type = 'DEVSEMINAR'
	}
	,$dbh);

	my $firstseminar = SQLValues(qq{
		select 
			to_char(min(expecteddate), 'YYYYMMDD')
		from 
			booktransaction 
		where 
			deleted is null
			and type='DEVSEMINAR'
	},$dbh);

	return ( 
	qq{
		<script type="text/javascript">
		var seminar_info = $seminarjsobj;
		var first_seminar = '$firstseminar';
		var last_seminar = '$lastseminar';
		var STATIC_HOST = '$ENV{STATIC_HOST}';
	} 
	. q{

var editkey = ''; //HACK to track the index of edited resource

function checkRows(textArea, minrows, viascript){
		
	if (viascript) { // this is a hack, an ugly hack
		textArea.rows = minrows;
		return;
	}

	if (minrows < 2) minrows = 2;
	
	while (
		textArea.rows >= minrows &&
		textArea.scrollHeight < textArea.offsetHeight){
	
		textArea.rows--;
	}

	while (textArea.scrollHeight > textArea.offsetHeight){
		textArea.rows++;
	}

	textArea.rows++;


	return;
}

function ShowCommentBox(classname) {

	checkRows(document.getElementById("LCCOMMENTBOX"), 2, true);
	
	var scroll_diff =  jQuery('#SHOWCOMMENTLINK').offset().top - 50;
	// hack, classname of the commentbox div is the id of the actual textarea
	jQuery("html, body").animate({ scrollTop: scroll_diff }, {duration: 300});

	jQuery('.' + classname).show(300);
	window.setTimeout(function(){ jQuery('#' + classname).focus(); }, 330 );
}

function HideCommentBox(commentboxid) {
	// hack, classname of the commentbox div is the id of the actual textarea
	if(!document.getElementById(commentboxid).value) {
				
				jQuery('.' + commentboxid).fadeOut(1000); 
	}
}

function ShowNewResources (classname) {
	
	checkRows(document.getElementById("NEWRESOURCELINK"), 2, true);
	checkRows(document.getElementById("NEWRESOURCEDESCRIPTION"), 4, true);
	var scroll_diff = jQuery('.NEWRESOURCEMENU').offset().top - 100;
	jQuery("html, body").animate({ scrollTop: scroll_diff }, {duration: 300});
	jQuery('.' + classname).show(300);
	window.setTimeout(function(){ jQuery('#NEWRESOURCENAME').focus(); }, 330 );
}

function HideNewResource(elementid) {
//I want to make element id available, albeit hacking through known IDs
	var name = document.getElementById('NEWRESOURCENAME');
	var link = document.getElementById('NEWRESOURCELINK');
	var description = document.getElementById('NEWRESOURCEDESCRIPTION');

	var id = document.activeElement.id;
	if (id == 'NEWRESOURCENAME' || id == 'NEWRESOURCELINK' || id == 'NEWRESOURCEDESCRIPTION' ) {
		return;
	}

	if (!(name.value || link.value || description.value)) {
			jQuery('.NEWRESOURCE').fadeOut(600);
	}
	
	return;
}

function getElementsByClass(searchClass,node,tag) {

	var classElements = new Array();

	if ( node == null )

		node = document;

	if ( tag == null )

		tag = '*';

	var els = node.getElementsByTagName(tag);

	var elsLen = els.length;

	var pattern = new RegExp('(^|\\\\s)'+searchClass+'(\\\\s|$)');

	for (i = 0, j = 0; i < elsLen; i++) {

		if ( pattern.test(els[i].className) ) {

	classElements[j] = els[i];

	j++;

		}

	}

	return classElements;

}

function dump(arr,level) {
	var dumped_text = "";
	if(!level) level = 0;

	var level_padding = "";
	for(var j=0;j<level+1;j++) level_padding += "    ";

	if(typeof(arr) == 'object') {
		for(var item in arr) {
			var value = arr[item];
			if(typeof(value) == 'object') {
				dumped_text += level_padding + "'" + item + "'             ...\n";
				dumped_text += dump(value,level+1);
			} else {
				dumped_text += level_padding + "'" + item + "' => \"" + value + "\"\n";
			}
		}
	} else {
		dumped_text = "===>"+arr+"<===("+typeof(arr)+")";
	}
	return dumped_text;
}
	
function GetTimeStamp() {
	var d=new Date();
	
	var datestr = GetMonthString(d.getMonth());
	datestr += ' ' + d.getDate();
	datestr += ', ' + d.getHours();
	datestr += ':' + ((d.getMinutes() < 10) ? ('0'+d.getMinutes()) : d.getMinutes());
	
	return datestr;
}    

function GetMonthString(monthint) {

	switch (monthint){
		case 0: return "Jan";
		break;
		
		case 1: return "Feb";
		break;

		case 2: return "Mar";
		break;

		case 3: return "Apr";
		break;

		case 4: return "May";
		break;

		case 5: return "June";
		break;

		case 6: return "July";
		break;
	
		case 7: return "Aug";
		break;

		case 8: return "Sept";
		break;

		case 9: return "Oct";
		break;

		case 10: return "Nov";
		break;

		case 11: return "Dec";
		break;
}
}

function GetSeminar(seminar_obj, which) {

	if ((seminar_obj['LAST'] && (which == 'NEXT' || which == 'LAST')) || (seminar_obj['FIRST'] && (which =='PREV' || which == 'FIRST'))) {
		
		return;
	}

	CallRemote({
		SUB: 'DevSeminar::GetRelevantSeminarInfo',
		ARGS:{
			CURRENTDATE: seminar_obj['PERLDATE'],
			WHICH: which
		},
		ONFINISH: function (seminar_info_str) {
			var temp_info = eval(seminar_info_str);
	
			if (temp_info['PERLDATE']) {
				seminar_info = temp_info;

			CallRemote({
				SUB: 'DevSeminar::Page::GetPage',
				ARGS:{
					DATE: seminar_info['PERLDATE'],
					WHICH: 'EQUALS'
				},
				ONFINISH: function (dynamic_html) {
					//document.getElementById('REPLACE_PAGE').innerHTML = dynamic_html;
					jQuery('#REPLACE_PAGE').fadeOut(100);
					window.setTimeout(function() {jQuery('#REPLACE_PAGE').html(dynamic_html);}, 150);
					window.setTimeout(function () {jQuery('#REPLACE_PAGE').fadeIn(200);}, 200);
				
					if (seminar_info['FIRST']) {
						$('.NAVLEFT').removeClass('HIGH');
					}
					if(seminar_info['LAST']) {
						$('.NAVRIGHT').removeClass('HIGH');
					}


				}
			});
			}
		},
		LOADINGINDICATORFLAG: 1
	});
}

function Enable (inp) {

	if (typeof inp == 'string') {
		document.getElementById(inp).disabled = false;
		return;
	}

	for (var i=0; i < inp.length; i++) {
		inp[i].disabled = false;
	}
}

function Disable(inp) {
	if (typeof inp == 'string') {
		document.getElementById(inp).disabled = true;
		return;
	}

	for (var i = 0 ; i < inp.length; i++) {
		inp.disabled = true;
	}
}

function ActiveCheck (navclass) {
	
	var activeflag = true;
	//alert(navclass+' '+seminar_info['PERLDATE']+' '+ first_seminar);

	if (navclass == 'NAVRIGHT' && seminar_info['PERLDATE'] == last_seminar) {
		activeflag =false;	
	}
	if (navclass=='NAVLEFT' && seminar_info['PERLDATE'] == first_seminar) {
		activeflag = false;
	}

	/*
	This portion of the code should override the CSS highlight effect if link is not active
	*/
	return activeflag;
}

function Like(anchor) {
	
	if (!seminar_info['TITLE']) return; 

	var current_likers =  seminar_info['LIKES'] ? seminar_info['LIKES'] : new Object();
	var current_user = seminar_info['USERFULLNAME'];
	
	var likekey = AssInList(current_user, current_likers);
	

	if(likekey) {
		delete current_likers[likekey];
	} else if (!likekey){
		current_likers[PushKey(current_likers)] = current_user;
	}
//alert('likekey '+likekey+'currentuser'+current_user+'\nlikers' +dump(current_likers));
	seminar_info['LIKES'] = current_likers;
	
	CallRemote({
		SUB: 'DevSeminar::PutSeminarInfo',
		ARGS: seminar_info,
		LOADINGINDICATORFLAG: 1,
		ONFINISH: function (seminar_obj) {
			//alert(seminar_obj);
			seminar_info = eval(seminar_obj);
			//alert(dump(seminar_info));	
			var args_likestr = new Object();
			args_likestr['LIKES'] = seminar_info['LIKES'];
			args_likestr['USERFULLNAME'] = seminar_info['USERFULLNAME'];
			
			CallRemote({
				SUB: 'Cookies::HtmlCookies::LikeStrings',
				ARGS: args_likestr,
				LOADINGINDICATORFLAG: 1,
				ONFINISH: function (like_obj) {
					//alert(dump(like_obj));
					var like_info = eval(like_obj);
					document.getElementById('ANCHORLIKE').innerHTML = like_info['LIKESTR'];
					document.getElementById('ANCHORLIKEALL').innerHTML = like_info['LIKEALL'];
				}
			});
			
		}
	});

}

function ShowLikes (el) {

	if (!seminar_info['TITLE']) return;

	var likers = seminar_info['LIKES'];
	var likeallstr = '';
	
	if (!likers || AssLength(likers) < 4) {
		return;
	}
	
	var i = 1;
	for (key in likers) {
		if (i==AssLength(likers)) break;
		likeallstr += likers[key] + ', ';
		i++;
	}

	likeallstr += ' and ' + likers[PushKey(likers) -1] + '</br></br>';

	var showlikers = document.getElementById('SHOWLIKERS');

	if (showlikers) {
		showlikers.innerHTML = likeallstr;
		
	} else {	
		showlikers = document.createElement('div');
		showlikers.setAttribute('id', 'SHOWLIKERS');
		showlikers.setAttribute('className', 'SHOWLIKERS');
		showlikers.style.display = 'none';
		showlikers.innerHTML = likeallstr;	
			
		document.getElementById('LCMENU').appendChild(showlikers);
	}
	
	if (showlikers.style.display == 'none') {
		showlikers.style.display = 'inline';
	} else {
		showlikers.style.display = 'none'
	}
	return;
}

function AddComment () {

	if (!seminar_info['TITLE']) return;

	var comment = new Object();
	comment['USER'] = seminar_info['USER'];
	comment['USERFULLNAME'] = seminar_info['USERFULLNAME'];
	comment['COMMENT'] = document.getElementById('LCCOMMENTBOX').value;
	comment['TIMESTAMP'] =  GetTimeStamp(); 
	var comments = seminar_info['COMMENTS'] ? seminar_info['COMMENTS'] : new Object();
	comments[PushKey(comments)] = comment;
	seminar_info['COMMENTS'] = comments;
	
	CallRemote({
		SUB: 'DevSeminar::PutSeminarInfo',
		ARGS: seminar_info,
		LOADINGINDICATORFLAG: 1,
		ONFINISH: function (seminar_obj) {
			seminar_info = eval(seminar_obj);
			var new_comment = seminar_info['COMMENTS'][AssLatest(seminar_info['COMMENTS'])];
			
			var args_arr = new Array();
			args_arr.push(seminar_info['USER']);
			args_arr.push(new_comment);
			args_arr.push(AssLatest(seminar_info['COMMENTS']));

			CallRemote({
				SUB: 'Cookies::HtmlCookies::NumberComments',
				ARGS: args_arr,
				LOADINGINDICATORFLAG: 1,
				ONFINISH: function (dynamic_html) {
					document.getElementById('ALLCOMMENTS').innerHTML = document.getElementById('ALLCOMMENTS').innerHTML + dynamic_html;
					document.getElementById('LCCOMMENTBOX').value = '';
					jQuery('.LCCOMMENTBOX' ).fadeOut(1000);
				}
			})
		}
	});
	
}

function DeleteComment (num) {
	
	if (!seminar_info['TITLE']) return;

	var key = num; 
	
	delete seminar_info['COMMENTS'][key];
	
	CallRemote({
		SUB: 'DevSeminar::PutSeminarInfo',
		ARGS: seminar_info,
		LOADINGINDICATORFLAG: 1,
		ONFINISH: function (seminar_obj) {
			seminar_info = eval(seminar_obj);
			var allcomments = document.getElementById('ALLCOMMENTS');
			var comment = document.getElementById('LCBLOCKCOMMENT' + key);
			allcomments.removeChild(comment);
		}
	});
}

function AddResource () {
	
	if (!seminar_info['TITLE']) return;

	var new_resource = new Object();
	new_resource['NAME'] = document.getElementById('NEWRESOURCENAME').value;
	new_resource['LINK'] = document.getElementById('NEWRESOURCELINK').value;
	new_resource['DESCRIPTION'] = document.getElementById('NEWRESOURCEDESCRIPTION').value;
	new_resource['USER'] = seminar_info['USER'];	
	new_resource['USERFULLNAME'] = seminar_info['USERFULLNAME'];

	if (!(TrimWhitespace(new_resource['LINK']) && TrimWhitespace(new_resource['DESCRIPTION']))) {
		alert('Please enter a Link and write a Description to add/update a resource');
		return;
	}
	var resources = seminar_info['RESOURCES'] ? seminar_info['RESOURCES'] : new Object();

	if (!editkey) {
		resources[PushKey(resources)] = new_resource;
	} else {
		resources[editkey] = new_resource;
	}
	
	seminar_info['RESOURCES'] = resources;

	CallRemote({
		SUB: 'DevSeminar::PutSeminarInfo',
		ARGS: seminar_info,
		LOADINGINDICATORFLAG: 1,
		ONFINISH: function (seminar_obj) {
			seminar_info = eval(seminar_obj);
			var args_arr = new Array();
			var which_resource = editkey ? editkey : AssLatest(seminar_info['RESOURCES']);
			var resource_args = seminar_info['RESOURCES'][which_resource];
			
			args_arr.push(resource_args);
			args_arr.push(which_resource);
			args_arr.push(seminar_info['USER']);
			
			CallRemote({
				SUB: 'Cookies::HtmlCookies::NumberResource',
				ARGS: args_arr,
				LOADINGINDICATORFLAG: 1,
				ONFINISH: function (dynamic_html) {
					var existing_resources = document.getElementById('EXISTINGRESOURCES');
					if (!editkey) {
						existing_resources.innerHTML += dynamic_html;	
					} else {
						document.getElementById('EXISTINGRESOURCE' + (editkey)).innerHTML = dynamic_html;
						editkey = '';
					}
					ClearHideResourceFields();
				}
			});

		}
	});
}

function ClearHideResourceFields () {
	document.getElementById("NEWRESOURCENAME").value = '';
	document.getElementById("NEWRESOURCELINK").value = '';
	document.getElementById("NEWRESOURCEDESCRIPTION").value = '';
	jQuery('.NEWRESOURCE').fadeOut(600);
}

function EditResource (key) {
	
	var selected_resource = seminar_info['RESOURCES'][key];
	editkey = key ;
	document.getElementById("NEWRESOURCENAME").value = selected_resource['NAME'];
	document.getElementById("NEWRESOURCELINK").value = selected_resource['LINK'];
	document.getElementById("NEWRESOURCEDESCRIPTION").value = selected_resource['DESCRIPTION'];
	ShowNewResources('NEWRESOURCE');
	return;
}

function FullName () {
}

/*
HACK overriding SwitchTabEnter() in a****lib.js
*/
function SwitchTabEnter() {
}

function AssInList (el, ass_arr) {
	
	if (AssLength(ass_arr) == 0) return '';

	for (key in ass_arr) {
		if (el == ass_arr[key]) return key;
	}
	return '';
}

function PushKey (ass_arr) {
	//alert(ass_arr);
	var push_key = AssLatest(ass_arr);
	return parseInt(push_key)+1;
}

function AssLatest (ass_arr) {

	var latest_key = 0;
	for(key in ass_arr) {
		if (parseInt(key) > latest_key) latest_key = key;
	}

	return parseInt(latest_key);
}

function AssLength (ass_arr) {
	var len = 0;
	for (key in ass_arr) {
		len++;
	}
	
	return len;
}


jQuery(document).ready(function () {
	
	jQuery('.NAVLEFT').hover(
		function(){ if (!seminar_info['FIRST']) { $(this).addClass('HIGH'); }},
		function(){ $(this).removeClass('HIGH'); }
	);
	jQuery('.NAVRIGHT').hover(
		function(){if (!seminar_info['LAST']){ $(this).addClass('HIGH');}},
		function() {$(this).removeClass('HIGH');}
	);

});


</script>

} 

);

}

=for
/*jQuery(document).ready( function(){

        jQuery('.NAVLEFT').hover(
                function(){

                        if (seminar_info['FIRST']) {
                                $(this).css({'cursor':'none', 'filter':'alpha(opacity=50)', 'border':'0px', 'height':'32px', 'width':'32px'});
                        } else  {
                                $(this).css({'cursor':'hand','filter':'alpha(opacity=100)', 'border':'0px', 'height':'32px', 'width':'45px'});
                        }
                },
                function(){
                        $(this).css({'cursor':'none', 'filter':'alpha(opacity=50)', 'border':'0px', 'height':'32px', 'width':'32px'});
                }
        );

        jQuery('.NAVRIGHT').hover(
                function () {
                        if(seminar_info['LAST']) {
                                $(this).css({'cursor':'none','filter':'alpha(opacity=50)', 'border':'0px', 'height':'32px', 'width':'32px'});
                        } else {
                                $(this).css({'cursor':'hand', 'filter':'alpha(opacity=100)', 'border':'0px', 'height':'32px', 'width':'45px'});
                        }
                },
                function () {
                        $(this).css({ 'cursor':'none','filter':'alpha(opacity=50)', 'border':'0px', 'height':'32px', 'width':'32px'});
                }
        );
});*/

=cut

1;
