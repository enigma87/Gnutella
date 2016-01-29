use lib "/home/kkishore/p4/intranet/perllib/A****";
package DevSeminar::Page;

use DevSeminar;
use A****U****;
use Cookies::HtmlCookies;

use warnings;
use strict;

my $dbh = DBConnect({
	INSTANCE => 'B***S1',
	SCHEMA => 'S***B**',
});

=for comment

# I wish we could put whole pages in intranet, with custom styling ;)
# Sad, it allows content only in <body> tag

sub GetPage {
	my $page = new DevSeminar('HTML', 'sharKishore');
	
	my $headerhtml = GetHeaderContent();
	$page->AddHeader($headerhtml);

	my $bodyhtml = GetBodyContent();
	$page->AddBody($bodyhtml);
	
	$page->PrintPage();
}

=cut

Expose('GetPage');
sub GetPage {
	my ($dbh, $args) = @_;
	my $date = $args->{DATE};
	my $which = $args->{WHICH};
	
	# I'm going to divide the page(body actually) into TITLE&INTERESTS and RESOURCES
	
	my $seminarinfo = DevSeminar::GetRelevantSeminarInfo($dbh, {
		CURRENTDATE => $date=~/^\d{8}/ ? $date : A****T****("YYYYMMDD"), 
		WHICH => $which ? $which : "CLOSEST",
	});
	
	my $seminarhtml = GetSeminar($dbh, $seminarinfo);
	my $resourceshtml = GetResources($dbh, $seminarinfo);
	
	my $pagehtml = qq{
		<div id="PAGE">
		<div class="RIGHT">
		$resourceshtml
		</div>	
		<div class="LEFT" >
		$seminarhtml
		</div>
		</div>
	};

}

sub GetSeminar {
	my ($dbh, $seminar) = @_;
	my $titleandinterests = GetTitleAndInterests($dbh, $seminar);
	my $likecomment = GetLikesAndComments($dbh, $seminar);	
	
	my $htmlcookiebox = $titleandinterests . $likecomment;
	return $htmlcookiebox;
}

sub GetTitleAndInterests {
	my ($dbh, $seminar) = @_;

	my @images = (
	"https://share.a****h****.com/HydraAttachments/wiki/39001/seminar-icon.png",
	"https://share.a****h****.com/HydraAttachments/wiki/39001/seminar_icon_2.jpg",
	"https://share.a****h****.com/HydraAttachments/wiki/39001/icon-seminar.png"
	);
	
	my $rand = int rand(3);

	my $htmlcookie  = qq{
		<div id="SEMINAR">
		<img class="HEADING" src="$images[$rand]" onclick="alert(dump(seminar_info))"/>	
		<h2 class="THISPAGE">$seminar->{TITLE}</h2></br>
		<h3 class="THISPAGE">$seminar->{GIST}</h3></br>
		<div>$seminar->{INTERESTS}</div>
		<p class="AUTHOR">$seminar->{AUTHOR}</p>
		<p> </p>
		<p> </p>
		<p> </p>
		</div>
	};

	return $htmlcookie;
}

sub GetLikesAndComments {
	my ($dbh, $seminar) = @_;
	my $htmlcookie = Cookies::HtmlCookies::LikeComment($dbh, $seminar);
	return $htmlcookie;
}

sub GetResources {
	my ($dbh, $seminar) = @_;

	my $htmlcookiebox = qq{
		<div id="RESOURCE">
		<img class="HEADING" src="https://share.a****h****.com/HydraAttachments/wiki/39001/hr_software_icon.jpg"/>
		<h2 class="THISPAGE">Resources</h2>
		<p><i>Do not pray for tasks equal to your powers; pray for powers equal to your tasks.<b style="float:right">Phillips Brooks</b></i></br></br></br></p>
	
	};

	$htmlcookiebox .= Cookies::HtmlCookies::Resources($dbh, $seminar);
	$htmlcookiebox .= '</div>';

	return $htmlcookiebox;
}

sub GetNavigationDevices {

	my $htmlcookie = qq{ 
		<table align="center" class="NAVIGATION">
		<tr>
	};
#		<td class="NAV"><a class="NAVLEFT" href="#"> <img onmouseover="ActiveCheck('NAVLEFT')" class="NAVIMAGE" src="https://share.a****h****.com/HydraAttachments/wiki/39001/resultset_first.png" onclick="GetSeminar(seminar_info, 'FIRST');" /></a></td>
#		<td class="NAV"><a class="NAVLEFT" href="#"> <img onmouseover="ActiveCheck('NAVLEFT')" class="NAVIMAGE" src="https://share.a****h****.com/HydraAttachments/wiki/39001/result_backward.png" onclick="GetSeminar(seminar_info, 'PREV');" /></a></td>
#		<td class="NAV"><a class="NAVRIGHT" href="#"> <img onmouseover="ActiveCheck('NAVRIGHT')" class="NAVIMAGE" src="https://share.a****h****.com/HydraAttachments/wiki/39001/result_forward.png" onclick="GetSeminar(seminar_info, 'NEXT');"/></a></td>
#		<td class="NAV"><a class="NAVRIGHT" href="#"> <img onmouseover="ActiveCheck('NAVRIGHT')" class="NAVIMAGE" src="https://share.a****h****.com/HydraAttachments/wiki/39001/resultset_last.png" onclick="GetSeminar(seminar_info, 'LAST');" /></a></td>
	
	$htmlcookie .= qq{
		<td class="NAV"><img class="NAVLEFT"  src="https://share.a****h****.com/HydraAttachments/wiki/39001/resultset_first.png" onclick="GetSeminar(seminar_info, 'FIRST');" /></td>
		<td class="NAV"><img id="TESTFUCK" class="NAVLEFT"  src="https://share.a****h****.com/HydraAttachments/wiki/39001/result_backward.png" onclick="GetSeminar(seminar_info, 'PREV');" /></td>
		<td class="NAV"><img class="NAVRIGHT" src="https://share.a****h****.com/HydraAttachments/wiki/39001/result_forward.png" onclick="GetSeminar(seminar_info, 'NEXT');"/></td>
		<td class="NAV"><img class="NAVRIGHT" src="https://share.a****h****.com/HydraAttachments/wiki/39001/resultset_last.png" onclick="GetSeminar(seminar_info, 'LAST');" /></td>
		</tr>
		</table>
	};
	
	return $htmlcookie;
}

1;

