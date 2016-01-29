use lib "/home/kkishore/p4/intranet/perllib/A****";
package DevSeminar;

use warnings;
use strict;
use A****U****;
use A****D****;
use SQL::Select;
use Switch;
use Data::Dumper;
use JSON;

sub GetSeminarInfo {
	return {
		USER => $Global::session{USERNAME} ,
		TITLE => 'CSS and CSS',
		GIST => 'CSS is elegant and maintainable than html styling!',
		DATE => 'MAR 7, 2012',
		AUTHOR => 'Kishore Kumar',
		INTERESTS => q{<p>Cascading Style Sheets (CSS) is a style sheet language used to describe the presentation semantics (the look and formatting) of a document written in a markup language. </p><p>Its most common application is to style web pages written in HTML and XHTML, but the language can also be applied to any kind of XML document, including plain XML, SVG and XUL.</p>},
		LIKES => [
			'kkishore',
			'ssubramaniam',
		],
		COMMENTS => {
			1 => {
				USER => 'kkishore',
				COMMENT => 'supercool',
				TIMESTAMP => '25 MAR, 23:30',
			},
			2 => { 	USER => 'ssubramaian',
				COMMENT => 'awesome',
				TIMESTAMP => '25 MAR, 23:35',
			},
		},
		RESOURCES => {
			1 => {
				NAME => 'Bugs are inevitable',
				LINK => 'http://www.ibm.com/developerworks/linux/library/l-pl-deb/index.html',
				DESCRIPTION => Unindent(qq{
					Bugs are as inevitable as death and taxes. 
				Nevertheless, the following material should help you avoid the pitfalls of bugs. 
				Some of the examples will require Perl 5.6.0 or at least 5.005. 
				If you want to try the Emacs examples, you may also need to install the Emacs editor.

				}),
				USER => 'kkishore',
			},
		},
		PERLDATE => 'YYYYMMDD',
		FIRST => '',
		LAST => '',
	};
}

Expose('GetRelevantSeminarInfo');
sub GetRelevantSeminarInfo {
	my ($dbh, $args) = @_;

	my $currentdate = $args->{CURRENTDATE};
	my $which = $args->{WHICH};

	my ($homedate, $targetdate);

	my $sql = SQL::Select->new(
		)->From(
			"booktransaction"
		);
	
	if ($which eq 'CLOSEST') {
		$sql->Where(
			["expecteddate >= to_date(?, 'YYYYMMDD')", $currentdate]
		);
	}
	
	if ($which eq 'EQUALS') {
		$sql->Select(
			"distinct to_char(expecteddate, 'YYYYMMDD')"
		)->Where(
			["expecteddate = to_date(?, 'YYYYMMDD')", $currentdate]
		);
	}
	
	if (InList($which, qw/LAST NEXT/)) {
		$sql->Where(
			[ "expecteddate > to_date(?, 'YYYYMMDD')", $currentdate]
		);
	} elsif (InList($which, qw/PREV FIRST/)) {
		$sql->Where(
			["expecteddate < to_date(?, 'YYYYMMDD')", $currentdate]
		);
	}
		
	if (InList($which, qw/LAST PREV/)) {
		$sql->Select(
			"to_char(max(distinct(expecteddate)), 'YYYYMMDD')"
		);
	} elsif (InList($which, qw/FIRST NEXT CLOSEST/)) {
		$sql->Select(
			"to_char(min(distinct(expecteddate)), 'YYYYMMDD')"
		);
	}
	
	$sql->Where(
		"type = 'DEVSEMINAR'",
		"deleted is null"
	);
	
	#$homedate = $sql->Values($dbh);

	$targetdate = $sql->Values($dbh);
	my $seminar_info = MergeAndGetSeminarInfo($dbh, $targetdate);
	
	# add a coupla userful fields
	my $namedbh = GetNameDBH();

	if (! SQLValues("select 1 from userprofile where windowsusername = ?", $namedbh, $Global::session{USERNAME})){
		$Global::session{USERNAME} = SQLValues("select windowsusername from userprofile where username=?", $namedbh, $Global::session{USERNAME});
	}
	
	$seminar_info->{USER} = $Global::session{USERNAME};
	
	my $lastdate = SQLValues("select to_char(max(expecteddate), 'YYYYMMDD') from booktransaction where type='DEVSEMINAR' and deleted is null", $dbh);
	my $firstdate = SQLValues("select to_char(min(expecteddate), 'YYYYMMDD') from booktransaction where type='DEVSEMINAR' and deleted is null", $dbh);

	$seminar_info->{LAST} = $lastdate eq $seminar_info->{PERLDATE};
	$seminar_info->{FIRST} = $firstdate eq $seminar_info->{PERLDATE};

	if ($namedbh) {
		$seminar_info->{USERFULLNAME} = SQLValues("select firstname || ' ' || lastname  from userprofile where windowsusername=?", $namedbh, $seminar_info->{USER});
	} else {
		$seminar_info->{USERFULLNAME} = $Global::session{USERNAME};
	}

	return $args->{JSON} ? PerlObjectToJScript($seminar_info) : $seminar_info;
}

sub MergeAndGetSeminarInfo {
	my ($dbh, $date) = @_ ;
	my @sorted_data = SQLColumnValues("select note from booktransaction where  type= 'DEVSEMINAR' and expecteddate = to_date(?, 'YYYYMMDD') and deleted is null order by id", $dbh, $date); 
	my $serializeddata;
	
	foreach my $note (@sorted_data) {
		$serializeddata .= $note;
	}
	$serializeddata =~ s/\$VAR1 =//;
	my $seminarinfo = eval $serializeddata;
	
	return $seminarinfo;
}

Expose('PutSeminarInfo');
sub PutSeminarInfo {
	my ($dbh, $seminar_info) = @_;
	


	#$seminar_info = JSON::jsonToObj($seminar_info);
	
	warn "\n\n put seminar info 1 : $seminar_info";

	return unless $seminar_info->{TITLE};
	
	my $relevantseminarinfo = GetRelevantSeminarInfo($dbh, { 
		CURRENTDATE => $seminar_info->{PERLDATE}, 
		WHICH => 'EQUALS',
	});

	if ($relevantseminarinfo->{PERLDATE}) {
		$seminar_info = MergeStructures($seminar_info, $relevantseminarinfo);
	}
	
#	foreach my $key (keys %{$seminar_info->{COMMENTS}}) {
#		$seminar_info->{COMMENTS}->{$key}->{COMMENT} = Text2HTML($seminar_info->{COMMENTS}->{$key}->{COMMENT}, {PRESERVESPACES => 1});
#	}
	
#	foreach my $key (keys %{$seminar_info->{RESOURCES}}) {
#		$seminar_info->{RESOURCES}->{$key}->{DESCRIPTION} = Text2HTML($seminar_info->{RESOURCES}->{$key}->{DESCRIPTION}, {PRESERVESPACES => 1});
#	}
#warn Dumper($seminar_info);
	DistributeAndPutSeminarInfo($dbh, $seminar_info);
	
	my $fresh_seminar = GetRelevantSeminarInfo($dbh, {
		CURRENTDATE => $seminar_info->{PERLDATE},
		WHICH => 'EQUALS',
		JSON => 1,
	});
	return $fresh_seminar;
}

sub MergeStructures {
	my ($bigbro, $smallbro) = @_;

	foreach my $big (keys %$bigbro) {
		$smallbro->{$big} = $bigbro->{$big};
	}

	return $smallbro;
}

sub DistributeAndPutSeminarInfo {
	my ($dbh, $seminar_info) = @_;
	
	DBTransaction(sub {
		my $serialized_data = Data::Dumper->new([$seminar_info])->Indent(0)->Dump;
		
		SQLDo("delete from sandbox.booktransaction where type = 'DEVSEMINAR' and trunc(expecteddate) = to_date(?, 'YYYYMMDD') and created < sysdate-7 ", $dbh, $seminar_info->{PERLDATE});
		SQLDo("update sandbox.booktransaction set deleted = sysdate, deletedby=? where type = 'DEVSEMINAR' and trunc(expecteddate) = to_date(?, 'YYYYMMDD') and created >= sysdate-7 ", $dbh, $Global::session{USERNAME}, $seminar_info->{PERLDATE});

		warn "\nsession name: " .  $Global::session{USERNAME} , "\n\n";

		while (length $serialized_data) {
			my $content = substr $serialized_data, 0, 3990, '';
			ProcessForm(
				"SANDBOX.BOOKTRANSACTION", 
				$dbh, 
				$Global::session{USERNAME}, 
				{
					NOTE => $content, 
					operation => 'Add',
					EXPECTEDDATE => "to_date('" . $seminar_info->{PERLDATE} . "', 'YYYYMMDD')",
					TYPE => 'DEVSEMINAR',
					USERNAME => substr($seminar_info->{TITLE}, 0, 40),
				}
			);
		}
	}, $dbh);

	return 'cool';
}


sub GetNameDBH {

	my $namedbh;
	eval {
		$namedbh = DBConnect({INSTANCE =>'A1', SCHEMA=>'a****1'});
		$namedbh = $namedbh ? $namedbh : DBConnect({INSTANCE =>'B1', SCHEMA=>'A****1'}) ;
		$namedbh = $namedbh ? $namedbh : DBConnect({INSTANCE =>'D1', SCHEMA=>'A****1'}) ;
		$namedbh = $namedbh ? $namedbh : DBConnect({INSTANCE =>'P1', SCHEMA=>'A****1'}) ;
	};	
	return $namedbh;
}

1;
