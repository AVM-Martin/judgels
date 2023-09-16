import { connect } from 'react-redux';

import { sendGAEvent } from '../../../../../../../../../../ga';
import { ProblemSubmissionCard } from '../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemSubmissionCard/ProblemSubmissionCard';
import { getGradingLanguageFamily } from '../../../../../../../../../../modules/api/gabriel/language.js';
import { selectCourse } from '../../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../../modules/courseChapterSelectors';
import { selectGradingLanguage } from '../../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import * as chapterProblemSubmissionActions from '../submissions/modules/chapterProblemSubmissionActions';
import * as webPrefsActions from '../../../../../../../../../../modules/webPrefs/webPrefsActions';

function ChapterProblemWorkspacePage({
  worksheet: { problem, worksheet },
  course,
  chapter,
  gradingLanguage,
  onCreateSubmission,
  onUpdateGradingLanguage,
}) {
  const { submissionConfig, reasonNotAllowedToSubmit } = worksheet;

  const createSubmission = async data => {
    onUpdateGradingLanguage(data.gradingLanguage);

    sendGAEvent({ category: 'Courses', action: 'Submit course problem', label: course.name });
    sendGAEvent({ category: 'Courses', action: 'Submit chapter problem', label: chapter.name });
    sendGAEvent({
      category: 'Courses',
      action: 'Submit problem',
      label: chapter.name + ': ' + problem.alis,
    });
    if (getGradingLanguageFamily(data.gradingLanguage)) {
      sendGAEvent({
        category: 'Courses',
        action: 'Submit language',
        label: getGradingLanguageFamily(data.gradingLanguage),
      });
    }

    return await onCreateSubmission(course.slug, chapter.jid, chapter.alias, problem.problemJid, problem.alias, data);
  };

  return (
    <ProblemSubmissionCard
      config={submissionConfig}
      onSubmit={createSubmission}
      reasonNotAllowedToSubmit={reasonNotAllowedToSubmit}
      preferredGradingLanguage={gradingLanguage}
    />
  );
}

const mapStateToProps = state => ({
  course: selectCourse(state),
  chapter: selectCourseChapter(state),
  gradingLanguage: selectGradingLanguage(state),
});

const mapDispatchToProps = {
  onCreateSubmission: chapterProblemSubmissionActions.createSubmission,
  onUpdateGradingLanguage: webPrefsActions.updateGradingLanguage,
};

export default connect(mapStateToProps, mapDispatchToProps)(ChapterProblemWorkspacePage);