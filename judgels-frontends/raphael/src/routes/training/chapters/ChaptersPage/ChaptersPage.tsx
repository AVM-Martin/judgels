import * as React from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { ChapterCreateDialog } from '../ChapterCreateDialog/ChapterCreateDialog';
import { ChapterEditDialog } from '../ChapterEditDialog/ChapterEditDialog';
import { ChaptersTable } from '../ChaptersTable/ChaptersTable';
import {
  Chapter,
  ChaptersResponse,
  ChapterCreateData,
  ChapterUpdateData,
} from '../../../../modules/api/jerahmeel/chapter';
import { ChapterProblemsResponse, ChapterProblemData } from '../../../../modules/api/jerahmeel/chapterProblem';
import * as chapterActions from '../modules/chapterActions';
import { ChapterProblemEditDialog } from '../ChapterProblemEditDialog/ChapterProblemEditDialog';

export interface ChapterPageProps {
  onGetChapters: () => Promise<ChaptersResponse>;
  onCreateChapter: (data: ChapterCreateData) => Promise<void>;
  onUpdateChapter: (chapterJid: string, data: ChapterUpdateData) => Promise<void>;
  onGetProblems: (chapterJid: string) => Promise<ChapterProblemsResponse>;
  onSetProblems: (chapterJid: string, data: ChapterProblemData[]) => Promise<void>;
}

export interface ChaptersPageState {
  response?: ChaptersResponse;
  isEditDialogOpen: boolean;
  isEditProblemsDialogOpen: boolean;
  editedChapter?: Chapter;
}

class ChaptersPage extends React.Component<ChapterPageProps, ChaptersPageState> {
  state: ChaptersPageState = {
    isEditDialogOpen: false,
    isEditProblemsDialogOpen: false,
  };

  componentDidMount() {
    this.refreshChapters();
  }

  render() {
    return (
      <ContentCard>
        <h3>Chapters</h3>
        <hr />
        {this.renderCreateDialog()}
        {this.renderEditDialog()}
        {this.renderEditProblemsDialog()}
        {this.renderChapters()}
      </ContentCard>
    );
  }

  private refreshChapters = async () => {
    const response = await this.props.onGetChapters();
    this.setState({ response });
  };

  private renderCreateDialog = () => {
    return <ChapterCreateDialog onCreateChapter={this.createChapter} />;
  };

  private renderEditDialog = () => {
    const { isEditDialogOpen, editedChapter } = this.state;
    return (
      <ChapterEditDialog
        isOpen={isEditDialogOpen}
        chapter={editedChapter}
        onUpdateChapter={this.updateChapter}
        onCloseDialog={() => this.editChapter(undefined)}
      />
    );
  };

  private renderEditProblemsDialog = () => {
    const { isEditProblemsDialogOpen, editedChapter } = this.state;
    return (
      <ChapterProblemEditDialog
        isOpen={isEditProblemsDialogOpen}
        chapter={editedChapter}
        onGetProblems={this.props.onGetProblems}
        onSetProblems={this.props.onSetProblems}
        onCloseDialog={() => this.editChapterProblems(undefined)}
      />
    );
  };

  private renderChapters = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: chapters } = response;
    if (chapters.length === 0) {
      return (
        <p>
          <small>No chapters.</small>
        </p>
      );
    }

    return (
      <ChaptersTable
        chapters={chapters}
        onEditChapter={this.editChapter}
        onEditChapterProblems={this.editChapterProblems}
      />
    );
  };

  private createChapter = async (data: ChapterCreateData) => {
    await this.props.onCreateChapter(data);
    await this.refreshChapters();
  };

  private editChapter = async (chapter?: Chapter) => {
    this.setState({
      isEditDialogOpen: !!chapter,
      editedChapter: chapter,
    });
  };

  private updateChapter = async (chapterJid: string, data: ChapterUpdateData) => {
    await this.props.onUpdateChapter(chapterJid, data);
    this.editChapter(undefined);
    await this.refreshChapters();
  };

  private editChapterProblems = async (chapter?: Chapter) => {
    this.setState({
      isEditProblemsDialogOpen: !!chapter,
      editedChapter: chapter,
    });
  };
}

const mapDispatchToProps = {
  onGetChapters: chapterActions.getChapters,
  onCreateChapter: chapterActions.createChapter,
  onUpdateChapter: chapterActions.updateChapter,
  onGetProblems: chapterActions.getProblems,
  onSetProblems: chapterActions.setProblems,
};
export default connect(undefined, mapDispatchToProps)(ChaptersPage);
