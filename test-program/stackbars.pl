use Chart::StackedBars;
$g = Chart::StackedBars->new;
$g->add_dataset ('foo', 'bar', 'junk', 'taco', 'karp');
$g->add_dataset (3, 4, 9, 10, 11);
$g->add_dataset (8, 6, 0, 12, 1);
$g->add_dataset (0, 7, 2, 13, 4);
$g->set ('title' => 'Stacked Bar Chart');
$g->set('y_grid_lines' => 'true');
$g->set('legend' => 'bottom');
$g->png ("stackedbars.png");