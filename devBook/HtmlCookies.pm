use lib '/home/kkishore/p4/intranet/perllib/A****';
package Cookies::HtmlCookies;
use warnings;
use strict;
use Data::Dumper;
use A****Utils;
use Switch;

# expects id, rows and cols (not as useful if id is not set)
# returns whole html for the textarea whic expands 
# with the amount of text in the box
##################################################################
sub AutoExpandTextArea {
	my ($rows, $width, $id, $onblur) = @_;
	
	$id = $id ? $id : "CommentBox";
	$rows = $rows > 2 ? $rows : 2;
	$width = $width ? $width : "35%";

	my $htmlcookie = qq{
		<textarea 
			id=$id 
			onkeyup="checkRows(this, $rows);"  
			rows="$rows" 
			style="overflow:hidden;width:$width;" 
			onblur="$onblur('}
	. uc($id) . qq{');"></textarea>};
}

sub Resources{
	my ($dbh, $args) = @_;
	my $htmlcookie;
	$args->{ID} = 'RESOURCES';
	
	my $existingresourceshtml = GetExistingResources($dbh, $args->{ID}, $args->{USER}, $args->{RESOURCES});
	#wrapping up to conveniently glue in new resource when added by users
	$existingresourceshtml = '<div id="EXISTINGRESOURCES">' . $existingresourceshtml . '</div>';

	my $prospectshtml = GetNewResources($dbh, $args->{ID}, $args->{USER});

	$htmlcookie = $existingresourceshtml . $prospectshtml;

	return $htmlcookie;
}

sub GetExistingResources {
	my ($dbh, $rid, $user, $resources) = @_;
	my $htmlcookie;
	
	foreach my $key (sort keys %$resources) {
	
		$htmlcookie .= NumberResource($dbh, [$resources->{$key}, $key, $user]);
	}
	
	return $htmlcookie;
}

Expose('NumberResource');
sub NumberResource {
	my ($dbh, $args) = @_;
	
	my ($resource, $number, $user) = @{$args};

	my $name = $resource->{NAME} || substr($resource->{DESCRIPTION}, 0, 30) || 'Resource unnamed - click to know more';
	
	my $description = Text2HTML($resource->{DESCRIPTION});

	my $htmlcookie = qq{<div class="EXISTINGRESOURCE" id="EXISTINGRESOURCE$number">};
	$htmlcookie .=	qq{<img src="https://share.a****h****.com/HydraAttachments/wiki/39001/edit.png" onclick="EditResource($number)" class="EDIT" />} if $user eq $resource->{USER};
	$htmlcookie .=	qq{<div class="RESOURCELINK"><a class="THISPAGE" target="_blank" href="$resource->{LINK}">$name</a></div> 
		<div class="RESOURCEDESC"></br></br>$description</br></br><span  class="RESOURCEFULUSER">$resource->{USERFULLNAME}</span><hr /></div>
		</div>
	};

	return $htmlcookie;
}

sub GetNewResources {
	my ($dbh, $rid, $user) = @_;
	my $htmlcookie = qq{<div class="NEWRESOURCEMENU"><a class="THISPAGE" href="#" onclick="ShowNewResources('NEWRESOURCE');">Add Resource</a></div>};

	$htmlcookie .= qq{<div id="NEWRESOURCEDIV" class="NEWRESOURCE" >}
		. q{<label class="RESOURCEHELP">Title</label>}
		#. qq{<input type="text" id="NEWRESOURCENAME" onfocus="this.hasFocus=true;" onblur="this.hasFocus=false;window.setTimeout(function() {HideNewResource('NEWRESOURCENAME') }, 100);" style="width:100%"/>}
		. qq{<input type="text" id="NEWRESOURCENAME" onblur="HideNewResource('NEWRESOURCENAME');" style="width:100%"/>}
		. q{<label class="RESOURCEHELP">Link</label>}
		. AutoExpandTextArea("2", "100%", "NEWRESOURCELINK", "HideNewResource")
		. q{<label class="RESOURCEHELP">Description</label>} 
		. AutoExpandTextArea("4", "100%", "NEWRESOURCEDESCRIPTION", "HideNewResource")
		. q{<button class="CANCEL" onclick="ClearHideResourceFields()" id="CANCELRESOURCE">Quit</button>}
		. q{<button class="SAVE" onclick="AddResource(null)" id="SAVERESOURCE" value="Save" >Save</button>} 
		. q{</div>};

	return $htmlcookie;
}

# expects
#	{
#		ID => "LIKECOMMENT",	
#		LIKES => ['user1', 'user2'],
#		COMMENTS => 
#		{
#			1 =>	{
#				USER => 'user',
#				COMMENT => 'comment',
#				TIMESTAMP => 'DD MON, MI:SS',
#			}
#		},
#		USER => "SESSION USER",
#	}
# 
# if null returns a fresh like and comment menu with ID = "LIKECOMMENT"
#
# details necessary for the page
######################################################################
sub LikeComment {
	my ($dbh, $args) = @_;
	$args->{ID} = 'LIKECOMMENT';
	my $menuhtml = LikeCommentMenu($dbh, $args->{ID}, $args->{USER},$args->{USERFULLNAME}, $args->{LIKES}, $args);
	my $commenthtml = Comments($dbh, $args->{ID}, $args->{USER}, $args->{COMMENTS});
	my $htmlcookie = $menuhtml . $commenthtml . '</div>';
	
	return $htmlcookie;
}

sub LikeCommentMenu {
	my ($dbh, $lcid, $user, $userfullname, $likes, $seminar) = @_;
	my $htmlcookie;
	my $likeref = LikeStrings($dbh, {USERFULLNAME => $userfullname, LIKES => $likes});
	
	$htmlcookie .= qq{
		<div class="$lcid" style="width:100%">
			<div class="LCMENU" id="LCMENU">
				<div class="LCMENULIKE"> <a class="THISPAGE" href="#" id='ANCHORLIKE'  onclick="Like(this)">$likeref->{LIKESTR}</a></div>
				<div class="LCMENUSHARE"> <a class="THISPAGE" href="mailto:?subject=Sharing: $seminar->{TITLE}&body=Hey, this seminar is on $seminar->{DATE} and it is about the TOPIC: $seminar->{TITLE} =\> $seminar->{GIST}. Cheers!" >Share&nbsp</a></div>
				<div class="LCBLOCKLIKE"  id="LCBLOCKLIKE" > <a class="THISPAGE" onclick="ShowLikes(this)" id='ANCHORLIKEALL' href="#">$likeref->{LIKEALL}</a> </div>
			</div>
	};
	
	return $htmlcookie;
}

Expose('LikeStrings');
sub LikeStrings {
	my ($dbh, $args) = @_;

	my $like;
	my $likestr = 'Like';
	my $likeall = '';
	
	warn "\n\nHell\n\n",Dumper($args);

	my %likehash = %{$args->{LIKES}} if $args->{LIKES}; # hack to get around JSON parsing issues of empty array
	my $likes = [ map { $likehash{$_} } keys %likehash ] if $args->{LIKES};
	my $user = $args->{USERFULLNAME};
	
	if ($likes && scalar(@$likes)) {
		if ($like = InList($user, @$likes)) {

			$like = $like =~ /^(\d+)/;
			$likestr = 'Unlike';
		}

		switch (scalar @$likes) {
			case 1 {
					$likeall = 'You like this' if $like;
					$likeall = "$likes->[0] likes this" unless $like;
			}
			case 2 {
					splice(@$likes, $like, 1) if $like;
					$likeall = "You and $likes->[0] like this" if $like;
					$likeall = EnglishJoin($dbh, {WORDS => $likes,}) unless $like;
					$likeall = $likeall . ' like this' unless $like;
			}
			case 3 {
					splice(@$likes, $like, 1) if $like;
					$likeall = EnglishJoin($dbh, {WORDS => $likes,});
					$likeall = 'You, ' . $likeall . ' like this' if $like;
					$likeall = $likeall . ' like this' unless $like;
			}
			else {
				return unless scalar @$likes;
				if ($like) {
					$likeall = 'You and ' . $#{$likes} . ' others like this';
				}
				else {
					$likeall = scalar @{$likes} . ' people like this';
				}
			}
		}
	}
	my $returnref = { LIKESTR => $likestr, LIKEALL => $likeall };
	return $args->{JSON}? PerlObjectToJScript($returnref) : $returnref;
}


sub Comments {
	my ($dbh, $lcid, $user, $comments) = @_;
	my $htmlcookie = '<div id="ALLCOMMENTS">';
	
	foreach my $key (sort keys %$comments) {
		$htmlcookie .= NumberComments($dbh, [$user, $comments->{$key}, $key]);
	}
	
	$htmlcookie .= '</div>';
	my $commentbox = AutoExpandTextArea("2", "100%", "LCCOMMENTBOX", "HideCommentBox");
	$htmlcookie .= q{<div><a class="THISPAGE" id="SHOWCOMMENTLINK" href="#" onclick="ShowCommentBox('LCCOMMENTBOX')">Add a Comment&nbsp</a></div>};#when comments get long
	$htmlcookie .= '<div id="COMMENTBOXDIV" class="LCCOMMENTBOX">' . $commentbox . qq{<button class="SAVE" onclick="AddComment()" id=SAVECOMMENT  value="Save" >Save</button> </div>};
	return $htmlcookie;
}

Expose('NumberComments');
sub NumberComments {
	my ($dbh, $args) = @_;
	my ($user, $comment, $number) = @{$args};

	my $usercomment = Text2HTML($comment->{COMMENT});

	my $htmlcookie = qq{ <div class="LCBLOCKCOMMENT" id="LCBLOCKCOMMENT$number"> };
	$htmlcookie .= qq{<img class="DELETE" onclick="DeleteComment($number)" src="$ENV{STATIC_HOST}/delete.png" />} if $user eq $comment->{USER};
	$htmlcookie .= qq{<label> <b class="LIKECOMMENTUSER">$comment->{USERFULLNAME}</b>\&nbsp\&nbsp$usercomment</br><span style="font-style:italic;font-size:12px;">$comment->{TIMESTAMP}</span></label></div>};	
	
	return $htmlcookie;
}

sub AjaxMasterLoadingDiv {

# well it is not a div - a hack again
	my $htmlcookie = qq{
		<img class="LOADING" id="loadingdiv" src="https://share.A****h****.com/HydraAttachments/wiki/39001/MasterLoadingIcon.gif" />
	};
	return $htmlcookie;
}
1;
	
